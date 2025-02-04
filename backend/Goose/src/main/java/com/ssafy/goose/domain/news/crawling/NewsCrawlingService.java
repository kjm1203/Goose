package com.ssafy.goose.domain.news.crawling;

import com.ssafy.goose.domain.news.dto.NewsArticleDto;
import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsCrawlingService {

    private final NewsRepository newsRepository;

    // 뉴스 기사 저장
    public void saveNewsArticle(NewsArticle article) {
        newsRepository.save(article);
    }

    // 특정 날짜 기사 조회 (Entity → DTO 변환 후 반환)
    public List<NewsArticleDto> getArticlesByDate(String pubDate) {
        return newsRepository.findByPubDate(pubDate)
                .stream()
                .map(NewsArticleDto::fromEntity)  // DTO 변환
                .collect(Collectors.toList());
    }

    // 특정 날짜 범위 조회 (Entity → DTO 변환 후 반환)
    public List<NewsArticleDto> getArticlesBetweenDates(String startDate, String endDate) {
        return newsRepository.findByPubDateBetween(startDate, endDate)
                .stream()
                .map(NewsArticleDto::fromEntity)  // DTO 변환
                .collect(Collectors.toList());
    }

    // 제목에 특정 키워드 포함된 기사 조회 (Entity → DTO 변환 후 반환)
    public List<NewsArticleDto> getArticlesByKeyword(String keyword) {
        return newsRepository.findByTitleRegex(keyword)
                .stream()
                .map(NewsArticleDto::fromEntity)  // DTO 변환
                .collect(Collectors.toList());
    }
}
