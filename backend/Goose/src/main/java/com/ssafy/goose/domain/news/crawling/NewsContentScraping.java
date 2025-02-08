package com.ssafy.goose.domain.news.crawling;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NewsContentScraping {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_NEWS_URL = "http://i12d208.p.ssafy.io:5050/extract"; // ✅ 뉴스 크롤링 FastAPI 서버 URL

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
                // 🔹 1. HTML 엔터티 디코딩 (`&lt;`, `&gt;`, `&#x27;` 등)
                String rawTitle = (String) result.get("title");
                String decodedTitle = StringEscapeUtils.unescapeHtml4(rawTitle); // ✅ HTML 엔터티 변환

                // 🔹 2. HTML 태그 제거 (`<b>`, `</b>` 등)
                String cleanTitle = Jsoup.parse(decodedTitle).text(); // ✅ HTML 태그 제거

                // 🔹 결과 반영
                result.put("title", cleanTitle);

                System.out.println("✅ [NewsContentScraping] 크롤링 성공");
                System.out.println("  📌 제목: " + cleanTitle);
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
