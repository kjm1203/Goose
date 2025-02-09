package com.ssafy.goose.domain.news.crawling;

import com.ssafy.goose.domain.news.storage.NewsStorageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AutoCrawlingService {
    private final NewsCrawlerService newsCrawlerService;
    private final NewsStorageService newsStorageService;

    public AutoCrawlingService(NewsCrawlerService newsCrawlerService, NewsStorageService newsStorageService) {
        this.newsCrawlerService = newsCrawlerService;
        this.newsStorageService = newsStorageService;
    }

    @Scheduled(cron = "0 0 0,6,12,13,14,15,16,17,18,19,20,21,22,23 * * *", zone = "Asia/Seoul")
    public void fetchAndSaveTrendingNews() {
        System.out.println("🕒 뉴스 크롤링 실행: " + LocalDateTime.now());

        // 1. 최신 인기 키워드들 가져오기
        List<String> trendingKeywords = newsCrawlerService.extractTrendingKeywords();

        for (String keyword : trendingKeywords) {
            System.out.println("🔍 검색어: " + keyword);

            // 2. 키워드로 뉴스 검색해서 가져오기
            Map<String, Object> newsData = newsCrawlerService.getNews(keyword);

            // 3. 뉴스 데이터를 갖고 메인 로직 수행 + 몽고DB 저장
            newsStorageService.saveNewsToMongoDB(newsData, keyword);
        }

        System.out.println("✅ 뉴스 저장 완료!");
    }
}
