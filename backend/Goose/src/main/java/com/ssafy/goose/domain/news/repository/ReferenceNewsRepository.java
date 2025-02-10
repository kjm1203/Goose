package com.ssafy.goose.domain.news.repository;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.entity.ReferenceNewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReferenceNewsRepository extends MongoRepository<ReferenceNewsArticle, String> {

    // ✅ 특정 키워드가 포함된 참고 뉴스 검색
    List<ReferenceNewsArticle> findByTitleContainingIgnoreCase(String keyword);

    @Query("{ 'publishedAt': { $gte: ?0 } }")
    List<NewsArticle> findRecentNews(LocalDateTime threeDaysAgo);
}
