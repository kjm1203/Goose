package com.ssafy.goose.domain.news.crawling;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AutoCrawlingService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private final NewsRepository newsRepository;

    private final NewsContentScraping newsContentScraping;

    private final String fastApiUrl = "http://localhost:5052/analyze-bias"; // FastAPI 서버 주소

    public AutoCrawlingService(NewsRepository newsRepository, NewsContentScraping newsContentScraping) {
        this.newsRepository = newsRepository;
        this.newsContentScraping = newsContentScraping;
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
    @Scheduled(cron = "0 0 0,6,12,18 * * *", zone = "Asia/Seoul")
    public void fetchAndSaveTrendingNews() {
        System.out.println("🕒 뉴스 크롤링 실행: " + LocalDateTime.now());

        List<String> trendingKeywords = extractTrendingKeywords(); // 🔹 최신 뉴스에서 키워드 추출

        for (String keyword : trendingKeywords) {
            System.out.println("🔍 검색어: " + keyword);
            Map<String, Object> newsData = getNews(keyword);
            saveNewsToMongoDB(newsData, keyword);
        }

        System.out.println("✅ 뉴스 저장 완료!");
    }

    /**
     * 🔹 5. MongoDB에 뉴스 저장 (본문 길이 100 이상 필터링 추가)
     */
    public void saveNewsToMongoDB(Map<String, Object> newsData, String keyword) {
        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

        for (Map<String, Object> item : newsItems) {
            String url = (String) item.get("link");
            Map<String, Object> scrapingResult = newsContentScraping.extractArticle(url);

            if (scrapingResult == null) {
                System.out.println("❌ 크롤링 실패: " + url);
                continue;
            }

            String content = (String) scrapingResult.get("text");
            if (content == null || content.length() < 100) {
                System.out.println("⚠️ 본문이 너무 짧아 저장하지 않음 (길이: " + (content != null ? content.length() : 0) + ")");
                continue;
            }

            // ✅ 같은 키워드의 기존 뉴스 기사 가져오기
            List<NewsArticle> relatedArticles = newsRepository.findByTitleRegex(keyword);
            List<String> existingContents = relatedArticles.stream()
                    .map(NewsArticle::getContent)
                    .collect(Collectors.toList());

            // ✅ FastAPI로 편향성 분석 요청
            Double biasScore = getBiasScore(existingContents, content, keyword);
            System.out.println("🔍 편향성: " + biasScore);

            NewsArticle article = NewsArticle.builder()
                    .title((String) item.get("title"))
                    .originalLink((String) item.get("originallink"))
                    .naverLink(url)
                    .description((String) item.get("description"))
                    .pubDate((String) item.get("pubDate"))
                    .content(content)  // 본문 크롤링 (100자 이상)
                    .topImage((String) scrapingResult.get("image")) // 대표 이미지
                    .extractedAt(LocalDateTime.now())
                    .biasScore(biasScore)
                    .build();


            newsRepository.save(article);
        }
    }

    private Double getBiasScore(List<String> contents, String targetContent, String keyword) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("keyword", keyword);
        requestBody.put("contents", contents);
        requestBody.put("target_content", targetContent);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("biasScore")) {
            return ((Number) response.getBody().get("biasScore")).doubleValue();
        }
        return 50.0; // 기본값 (편향성 중립)
    }
}
