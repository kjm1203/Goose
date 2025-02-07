package com.ssafy.goose.domain.news.crawling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsCrawlerService {
    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> extractTrendingKeywords() {
        String url = "https://openapi.naver.com/v1/search/news.json?query=뉴스&display=50&sort=date";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) response.getBody().get("items");
        return newsItems.stream()
                .map(item -> ((String) item.get("title")).replaceAll("<[^>]*>", "")) // HTML 태그 제거
                .collect(Collectors.toList());
    }

    public Map<String, Object> getNews(String query) {
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + query + "&display=20&sort=sim";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}
