package com.ssafy.goose.domain.news.repository;

import com.ssafy.goose.domain.news.entity.ReferenceNewsArticle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReferenceNewsCustomRepository {

    private final MongoTemplate mongoTemplate;

    public ReferenceNewsCustomRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<ReferenceNewsArticle> findNewsByKeywords(String[] keywords, LocalDateTime since) {
        Query query = new Query();

        // ✅ 3일 이내의 뉴스만 검색
        // query.addCriteria(Criteria.where("pubDate").gte(since));

        // ✅ OR 조건을 사용하여 title, description, content 중 하나라도 포함된 뉴스 검색
        List<Criteria> keywordCriteriaList = new ArrayList<>();
        for (String keyword : keywords) {
            keywordCriteriaList.add(new Criteria().orOperator(
                    Criteria.where("title").regex(".*" + keyword + ".*", "i"),
                    Criteria.where("description").regex(".*" + keyword + ".*", "i"),
                    Criteria.where("content").regex(".*" + keyword + ".*", "i")
            ));
        }

        if (!keywordCriteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(keywordCriteriaList.toArray(new Criteria[0])));
        }

        // ✅ 검색 결과 최대 5개 제한
        query.limit(5);

        List<ReferenceNewsArticle> mongoData = mongoTemplate.find(query, ReferenceNewsArticle.class, "reference_news");

        // ✅ 검색된 데이터 개수 출력 (디버깅용)
        System.out.println("🔍 MongoDB에서 검색된 데이터: " + mongoData.size() + "개");

        return mongoData;
    }
}
