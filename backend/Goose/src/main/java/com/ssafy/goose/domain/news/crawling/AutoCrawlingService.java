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

        List<String> trendingKeywords = newsCrawlerService.extractTrendingKeywords();

        for (String keyword : trendingKeywords) {
            System.out.println("🔍 검색어: " + keyword);
            Map<String, Object> newsData = newsCrawlerService.getNews(keyword);
            newsStorageService.saveNewsToMongoDB(newsData, keyword);
        }

        System.out.println("✅ 뉴스 저장 완료!");
    }
}
