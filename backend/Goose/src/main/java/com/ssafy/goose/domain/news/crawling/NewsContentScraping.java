package com.ssafy.goose.domain.news.crawling;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NewsContentScraping {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_NEWS_URL = "http://localhost:5002/extract"; // ✅ Newspaper3k FastAPI 서버 주소

    /**
     * 🔹 FastAPI (Newspaper3k) 호출하여 뉴스 본문과 대표 이미지를 가져옴
     */
    public Map<String, Object> extractArticle(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url", url);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("🔍 [NewsContentScraping] FastAPI 요청 시작: " + url);
            ResponseEntity<Map> response = restTemplate.exchange(FASTAPI_NEWS_URL, HttpMethod.POST, entity, Map.class);
            Map<String, Object> result = response.getBody();

            // ✅ 크롤링 성공 로그
            if (result != null) {
                System.out.println("✅ [NewsContentScraping] 크롤링 성공");
                System.out.println("  📌 제목: " + result.get("title"));
                System.out.println("  📌 본문 (앞부분): " + ((String) result.get("text")).substring(0, Math.min(200, ((String) result.get("text")).length())) + "...");
                System.out.println("  📌 대표 이미지: " + result.get("image"));
            } else {
                System.out.println("⚠️ [NewsContentScraping] 크롤링 결과가 null입니다.");
            }

            return result;
        } catch (Exception e) {
            System.err.println("❌ [NewsContentScraping] 크롤링 실패: " + e.getMessage());
            return null;
        }
    }
}
