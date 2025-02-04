package com.ssafy.goose.domain.news.repository;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends MongoRepository<NewsArticle, String> {
    List<NewsArticle> findByTitleContaining(String keyword);  // 제목 검색 기능
}
