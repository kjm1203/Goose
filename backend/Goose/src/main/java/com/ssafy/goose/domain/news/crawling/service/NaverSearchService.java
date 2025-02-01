package com.ssafy.goose.domain.news.crawling.service;

import com.ssafy.goose.domain.news.crawling.model.NewsArticle;
import com.ssafy.goose.domain.news.crawling.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NaverSearchService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.datalab-url}")
    private String datalabUrl; // 네이버 데이터랩 검색어 트렌드 API URL

    private final RestTemplate restTemplate = new RestTemplate();
    private final NewsArticleRepository newsRepository; // MongoDB 저장소

    public NaverSearchService(NewsArticleRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * 🔹 1. 네이버 데이터랩에서 인기 검색어 가져오기
     */
    public List<String> getTrendingKeywords() {
        String url = datalabUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
        List<String> keywords = new ArrayList<>();

        for (Map<String, Object> item : results) {
            keywords.add((String) item.get("title"));
        }

        return keywords;
    }

    /**
     * 🔹 2. 네이버 뉴스 검색 API 호출
     */
    public Map<String, Object> getNews(String query) {
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + query
                + "&display=20&sort=sim";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }

    /**
     * 🔹 3. 인기 검색어 기반으로 뉴스 크롤링 후 MongoDB 저장
     */
    @Scheduled(cron = "0 0 6,12,18,0 * * *", zone = "Asia/Seoul")
    public void fetchAndSaveTrendingNews() {
        List<String> trendingKeywords = getTrendingKeywords(); // 인기 검색어 가져오기

        for (String keyword : trendingKeywords) {
            System.out.println("🔍 인기 검색어 뉴스 크롤링: " + keyword);
            Map<String, Object> newsData = getNews(keyword);

            List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

            for (Map<String, Object> item : newsItems) {
                NewsArticle article = new NewsArticle(
                        (String) item.get("title"),
                        (String) item.get("originallink"),
                        (String) item.get("link"),
                        (String) item.get("description"),
                        (String) item.get("pubDate"),
                        null, // 뉴스 본문 (추후 goose3 활용 가능)
                        null, // 대표 이미지 (추후 goose3 활용 가능)
                        LocalDateTime.now()
                );

                newsRepository.save(article);
            }
        }
    }
}
