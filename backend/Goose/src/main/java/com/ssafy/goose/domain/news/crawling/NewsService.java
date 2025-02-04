package com.ssafy.goose.domain.news.crawling;

import com.ssafy.goose.domain.news.model.NewsArticleDto;
import com.ssafy.goose.domain.news.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsArticleRepository newsRepository;

    // 뉴스 기사 저장
    public void saveNewsArticle(NewsArticleDto article) {
        newsRepository.save(article);
    }

    // 특정 날짜 기사 조회
    public List<NewsArticleDto> getArticlesByDate(String pubDate) {
        return newsRepository.findByPubDate(pubDate);
    }

    // 특정 날짜 범위 조회
    public List<NewsArticleDto> getArticlesBetweenDates(String startDate, String endDate) {
        return newsRepository.findByPubDateBetween(startDate, endDate);
    }

    // 제목에 특정 키워드 포함된 기사 조회
    public List<NewsArticleDto> getArticlesByKeyword(String keyword) {
        return newsRepository.findByTitleRegex(keyword);
    }
}
