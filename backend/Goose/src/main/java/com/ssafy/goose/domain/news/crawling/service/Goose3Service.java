package com.ssafy.goose.domain.news.crawling.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Goose3Service {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_GOOSE3_URL = "http://localhost:5001/extract"; // FastAPI 서버 주소

    /**
     * 🔹 FastAPI를 호출하여 뉴스 본문과 대표 이미지를 가져옴
     */
    public Map<String, Object> extractArticle(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url", url);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(FASTAPI_GOOSE3_URL, HttpMethod.POST, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("❌ [Goose3Service] 크롤링 실패: " + e.getMessage());
            return null;
        }
    }
}
