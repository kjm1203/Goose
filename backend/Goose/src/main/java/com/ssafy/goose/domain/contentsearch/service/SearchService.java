package com.ssafy.goose.domain.contentsearch.service;

import com.ssafy.goose.domain.contentsearch.dto.NewsResponseDto;
import com.ssafy.goose.domain.contentsearch.entity.News;
import com.ssafy.goose.domain.contentsearch.external.InternetSearchService;
import com.ssafy.goose.domain.contentsearch.repository.jpa.ContentNewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final ContentNewsRepository contentNewsRepository;
    private final InternetSearchService internetSearchService;

    @Autowired
    public SearchService(ContentNewsRepository contentNewsRepository, InternetSearchService internetSearchService) {
        this.contentNewsRepository = contentNewsRepository;
        this.internetSearchService = internetSearchService;
    }

    public List<NewsResponseDto> searchNewsByKeyword(String[] keywords) {
        List<NewsResponseDto> allResults = new ArrayList<>();

        for (String keyword : keywords) {
            // MongoDB에 해당 키워드로 뉴스 검색
            List<News> newsList = contentNewsRepository.searchByTitleOrDescriptionOrContent(keyword);
            if (!newsList.isEmpty()) {
                // ✅ newsAgency 보완 후 추가
                allResults.addAll(convertNewsToNewsResponse(newsList));
            } else {
                // MongoDB에 검색 결과가 없으면 네이버 API 검색
                allResults.addAll(internetSearchService.search(keyword));
            }
        }

        return allResults;
    }

    private List<NewsResponseDto> convertNewsToNewsResponse(List<News> newsList) {
        List<NewsResponseDto> newsResponseDtos = new ArrayList<>();
        for (News news : newsList) {
            String newsAgency = news.getNewsAgency();

            // ✅ newsAgency가 비어 있으면 크롤링을 통해 보완
            if (newsAgency == null || newsAgency.equals("Unknown")) {
                newsAgency = internetSearchService.extractNewsAgency(news.getOriginalLink());
            }

            newsResponseDtos.add(new NewsResponseDto(
                    news.getTitle(),
                    news.getOriginalLink(),
                    news.getNaverLink(),
                    news.getDescription(),
                    news.getPubDate(),
                    news.getParagraphs(),
                    news.getContent(),
                    news.getTopImage(),
                    newsAgency,
                    news.getExtractedAt()
            ));
        }
        return newsResponseDtos;
    }
}
