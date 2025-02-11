package com.ssafy.goose.domain.contentsearch.external;

import com.ssafy.goose.domain.contentsearch.dto.NewsResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

@Service
public class NewsSearchService implements InternetSearchService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private static final String NAVER_NEWS_URL = "https://openapi.naver.com/v1/search/news.json?query=";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<NewsResponseDto> search(String keyword) {
        // 1️⃣ 입력받은 키워드 처리 (쉼표, 공백으로 구분하여 배열 처리)
        String[] keywords = keyword.split("\\s*,\\s*|\\s+");

        // 2️⃣ MongoDB 검색
        Query query = new Query();
        Criteria criteria = new Criteria();
        Criteria[] keywordCriterias = new Criteria[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            keywordCriterias[i] = Criteria.where("content").regex(".*" + keywords[i] + ".*", "i");
        }
        criteria.andOperator(keywordCriterias);
        query.addCriteria(criteria);

        List<NewsResponseDto> mongoData = mongoTemplate.find(query, NewsResponseDto.class, "reference_news");

        int mongoDataSize = mongoData.size();
        int neededFromNaver = 5 - mongoDataSize;

        // 3️⃣ MongoDB 데이터 부족 시 Naver API 호출
        // MongoDB에서 찾은 뉴스 데이터가 5개 미만일 경우, Naver 뉴스 API를 호출하여 부족한 데이터 채움.
        List<NewsResponseDto> resultData = new ArrayList<>(mongoData);
        List<NewsResponseDto> analysisData = new ArrayList<>();

        if (mongoDataSize < 5) {
            List<NewsResponseDto> naverData = fetchNaverNews(keyword);
            resultData.addAll(naverData.subList(0, Math.min(neededFromNaver, naverData.size())));
            analysisData.addAll(naverData.subList(Math.min(neededFromNaver, naverData.size()), naverData.size()));
        }

        if (resultData.size() > 5) {
            resultData = resultData.subList(0, 5);
        }

        return resultData;
    }

    private List<NewsResponseDto> fetchNaverNews(String keyword) {
        // Naver 뉴스 API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                NAVER_NEWS_URL + keyword + "&display=55",
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray items = jsonResponse.getJSONArray("items");

        List<NewsResponseDto> newsList = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject jsonObject = items.getJSONObject(i);
            newsList.add(new NewsResponseDto(
                    jsonObject.getString("title"),
                    jsonObject.getString("link"),
                    jsonObject.getString("link"),
                    jsonObject.getString("description"),
                    jsonObject.getString("pubDate"),
                    "",
                    "",
                    "",
                    null
            ));
        }

        return newsList;
    }
}
