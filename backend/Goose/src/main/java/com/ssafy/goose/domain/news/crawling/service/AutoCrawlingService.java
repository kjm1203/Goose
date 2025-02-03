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
import java.util.stream.Collectors;

@Service
public class AutoCrawlingService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final NewsArticleRepository newsRepository;
    private final NewsContentScraping newsContentScraping; // ✅ 변경: Newspaper3k 크롤링 서비스

    public AutoCrawlingService(NewsArticleRepository newsRepository, NewsContentScraping newsContentScraping) {
        this.newsRepository = newsRepository;
        this.newsContentScraping = newsContentScraping; // ✅ 변경
    }

    /**
     * 🔹 1. 네이버 뉴스 API로 최신 뉴스 제목에서 키워드 추출
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
                .map(item -> ((String) item.get("title")).replaceAll("<[^>]*>", "")) // HTML 태그 제거
                .collect(Collectors.toList());

        return extractKeywordsFromTitles(titles);
    }

    /**
     * 🔹 2. 뉴스 제목에서 주요 키워드 추출 (빈도 기반)
     */
    private List<String> extractKeywordsFromTitles(List<String> titles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : titles) {
            String[] words = title.split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^가-힣a-zA-Z0-9]", "").trim(); // 특수문자 제거
                if (word.length() > 1) { // 1글자 이하 단어 제외
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }

        return wordCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // 빈도수 기준 정렬
                .limit(10) // 상위 10개 키워드 선택
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 🔹 3. 네이버 뉴스 API로 뉴스 데이터 가져오기
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
     * 🔹 4. 6시간마다 자동 실행하여 MongoDB에 뉴스 저장
     */
    @Scheduled(cron = "0 40 18 * * *", zone = "Asia/Seoul")
    public void fetchAndSaveTrendingNews() {
        System.out.println("🕒 뉴스 크롤링 실행: " + LocalDateTime.now());

        List<String> trendingKeywords = extractTrendingKeywords(); // 🔹 최신 뉴스에서 키워드 추출

        for (String keyword : trendingKeywords) {
            System.out.println("🔍 검색어: " + keyword);
            Map<String, Object> newsData = getNews(keyword);
            saveNewsToMongoDB(newsData);
        }

        System.out.println("✅ 뉴스 저장 완료!");
    }

    /**
     * 🔹 5. MongoDB에 뉴스 저장
     */
    public void saveNewsToMongoDB(Map<String, Object> newsData) {
        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

        for (Map<String, Object> item : newsItems) {
            String url = (String) item.get("link");
            Map<String, Object> newsResult = newsContentScraping.extractArticle(url); // ✅ 변경: Goose3 → Newspaper3k

            NewsArticle article = new NewsArticle(
                    (String) item.get("title"),
                    (String) item.get("originallink"),
                    url,
                    (String) item.get("description"),
                    (String) item.get("pubDate"),
                    newsResult != null ? (String) newsResult.get("text") : null,  // ✅ 본문 크롤링
                    newsResult != null ? (String) newsResult.get("image") : null, // ✅ 이미지 크롤링
                    LocalDateTime.now()
            );

            newsRepository.save(article);
        }
    }
}
