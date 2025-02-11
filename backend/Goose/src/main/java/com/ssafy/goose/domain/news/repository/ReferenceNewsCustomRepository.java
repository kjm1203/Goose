package com.ssafy.goose.domain.news.repository;

import com.ssafy.goose.domain.news.entity.ReferenceNewsArticle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ReferenceNewsCustomRepository {

    private final MongoTemplate mongoTemplate;

    public ReferenceNewsCustomRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<ReferenceNewsArticle> findNewsByKeywords(List<String> keywords, LocalDateTime since) {
        Query query = new Query();

        // ✅ 3일 이내의 뉴스만 조회 + LocalDateTime → Date 변환
        Date sinceDate = java.sql.Timestamp.valueOf(since);
//        query.addCriteria(Criteria.where("pubDate").gte(sinceDate));

        // ✅ OR 조건으로 하나라도 포함된 뉴스 검색
        List<Criteria> keywordCriteriaList = new ArrayList<>();
        for (String keyword : keywords) {
            keywordCriteriaList.add(new Criteria().orOperator(
                    Criteria.where("title").regex(".*" + keyword + ".*", "i"),
                    Criteria.where("content").regex(".*" + keyword + ".*", "i")
            ));
        }

        System.out.println("키워드가 포함된 뉴스 갯수 : " + keywordCriteriaList.size());

        if (!keywordCriteriaList.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(keywordCriteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, ReferenceNewsArticle.class, "reference_news");
    }
}
