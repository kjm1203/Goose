package com.ssafy.goose.domain.contentsearch.external;

import com.ssafy.goose.domain.contentsearch.dto.NewsResponseDto;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ArrayList;

@Service
public class NewsSearchService implements InternetSearchService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    private static final String NAVER_NEWS_URL = "https://openapi.naver.com/v1/search/news.json?query=";

    @Autowired
    private MongoTemplate mongoTemplate;

    // ✅ SSL 인증 우회 설정 추가 (애플리케이션 시작 시 자동 실행)
    @PostConstruct
    public void init() {
        trustAllCertificates();
    }

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
        List<NewsResponseDto> resultData = new ArrayList<>(mongoData);
        if (mongoDataSize < 5) {
            List<NewsResponseDto> naverData = fetchNaverNews(keyword);
            resultData.addAll(naverData.subList(0, Math.min(neededFromNaver, naverData.size())));
        }

        if (resultData.size() > 5) {
            resultData = resultData.subList(0, 5);
        }

        return resultData;
    }

    private List<NewsResponseDto> fetchNaverNews(String keyword) {
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

            String originalLink = jsonObject.optString("originallink", jsonObject.optString("link", ""));
            String naverLink = jsonObject.optString("link", "");

            // ✅ 네이버 뉴스 URL이 있다면 네이버 페이지 크롤링
            String newsAgency = (!naverLink.isEmpty() && naverLink.contains("news.naver.com"))
                    ? extractNewsAgency(naverLink)
                    : extractNewsAgency(originalLink);

            newsList.add(new NewsResponseDto(
                    jsonObject.optString("title", "Unknown"),
                    originalLink,
                    naverLink,
                    jsonObject.optString("description", ""),
                    jsonObject.optString("pubDate", ""),
                    "",
                    "",
                    "",
                    newsAgency,
                    null
            ));
        }

        return newsList;
    }

    // ✅ 네이버 뉴스 페이지에서 언론사 정보 가져오기
    public String extractNewsAgency(String newsUrl) {
        try {
            if (newsUrl == null || newsUrl.isEmpty()) {
                return "Unknown";
            }

            // Jsoup을 사용하여 HTML 가져오기
            Document doc = Jsoup.connect(newsUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Referer", "https://www.naver.com/")
                    .timeout(5000)
                    .get();

            System.out.println("✅ [디버깅] 크롤링된 페이지 URL: " + newsUrl);

            // ✅ 1. 언론사 로고 이미지 (`title` 속성)에서 가져오기 (최우선)
            Element logoImg = doc.selectFirst("img.media_end_head_top_logo_img");
            if (logoImg != null) {
                String newsAgency = logoImg.attr("title").trim();
                if (!newsAgency.isEmpty()) {
                    return newsAgency;
                }
            }

            // ✅ 2. `meta[property=og:site_name]` 확인
            Element metaSiteName = doc.selectFirst("meta[property=og:site_name]");
            if (metaSiteName != null) {
                String content = metaSiteName.attr("content").trim();
                if (!content.isEmpty()) {
                    return content;
                }
            }

            // ✅ 3. 도메인 기반 언론사 추정
            return estimateNewsAgencyFromUrl(newsUrl);

        } catch (Exception e) {
            return estimateNewsAgencyFromUrl(newsUrl);
        }
    }

    // ✅ 크롤링 실패 시 도메인 기반 언론사 추정
    private String estimateNewsAgencyFromUrl(String newsUrl) {
        if (newsUrl.contains("jtbc")) return "JTBC 뉴스";
        if (newsUrl.contains("kmib")) return "국민일보";
        if (newsUrl.contains("joongang")) return "중앙일보";
        if (newsUrl.contains("kado")) return "강원도민일보";
        if (newsUrl.contains("dailian")) return "데일리안";
        if (newsUrl.contains("tf")) return "더팩트";
        return "Unknown";
    }

    // ✅ SSL 인증 우회 설정
    private static void trustAllCertificates() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{ new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
