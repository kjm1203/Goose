package com.ssafy.goose.domain.news.crawling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsCrawlerService {
    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 🔹 1. 네이버 뉴스 API에서 최신 뉴스 제목 가져오기
     */
    public List<String> extractTrendingKeywords() {
        String url = "https://openapi.naver.com/v1/search/news.json?query=뉴스&display=50&sort=date";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) response.getBody().get("items");
        List<String> titles = newsItems.stream()
                .map(item -> ((String) item.get("title")).replaceAll("<[^>]*>", "")) // ✅ HTML 태그 제거
                .collect(Collectors.toList());

        // 🔹 2. 뉴스 제목에서 주요 키워드 추출 (빈도 기반)
        return extractKeywordsFromTitles(titles);
    }

    /**
     * 🔹 2. 뉴스 제목에서 주요 키워드 추출 (빈도 기반)
     */
    private List<String> extractKeywordsFromTitles(List<String> titles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : titles) {
            String[] words = title.split("\\s+"); // 띄어쓰기 기준으로 단어 분리
            for (String word : words) {
                word = word.replaceAll("[^가-힣a-zA-Z0-9]", "").trim(); // ✅ 특수문자 제거
                if (word.length() > 1) { // ✅ 1글자 이하 단어 제외
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }

        // 🔹 상위 10개 키워드 반환 (빈도수 기준 정렬)
        return wordCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // ✅ 빈도수 기준 내림차순 정렬
                .limit(10) // ✅ 상위 10개 키워드 선택
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 🔹 3. 특정 키워드 기반 뉴스 검색
     */
    public Map<String, Object> getNews(String query, int displayCount) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + encodedQuery + "&display="+ displayCount +"&sort=sim";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}
