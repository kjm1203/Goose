package com.ssafy.goose.domain.news.crawling.service;

import com.ssafy.goose.domain.news.crawling.model.NewsArticle;
import com.ssafy.goose.domain.news.crawling.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsArticleRepository newsRepository;

    // 뉴스 기사 저장
    public void saveNewsArticle(NewsArticle article) {
        newsRepository.save(article);
    }

    // 특정 날짜 기사 조회
    public List<NewsArticle> getArticlesByDate(String pubDate) {
        return newsRepository.findByPubDate(pubDate);
    }

    // 특정 날짜 범위 조회
    public List<NewsArticle> getArticlesBetweenDates(String startDate, String endDate) {
        return newsRepository.findByPubDateBetween(startDate, endDate);
    }

    // 제목에 특정 키워드 포함된 기사 조회
    public List<NewsArticle> getArticlesByKeyword(String keyword) {
        return newsRepository.findByTitleRegex(keyword);
    }
}
