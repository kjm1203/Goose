package com.ssafy.goose.domain.news.crawling.repository;

import com.ssafy.goose.domain.news.crawling.model.NewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends MongoRepository<NewsArticle, String> {

    // 특정 날짜의 기사 조회
    List<NewsArticle> findByPubDate(String pubDate);

    // 특정 날짜 범위 기사 조회
    List<NewsArticle> findByPubDateBetween(String startDate, String endDate);

    // 제목에 특정 키워드 포함된 기사 조회 (Regex)
    List<NewsArticle> findByTitleRegex(String keyword);
}
