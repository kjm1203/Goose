package com.ssafy.goose.domain.factcheck.scheduler;

import com.ssafy.goose.domain.factcheck.crawling.FactCheckCrawlerService;
import com.ssafy.goose.domain.factcheck.storage.FactCheckStorageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AutoFactCheckCrawlingService {
    private final FactCheckCrawlerService factCheckCrawlerService;
    private final FactCheckStorageService factCheckStorageService;

    public AutoFactCheckCrawlingService(FactCheckCrawlerService factCheckCrawlerService, FactCheckStorageService factCheckStorageService) {
        this.factCheckCrawlerService = factCheckCrawlerService;
        this.factCheckStorageService = factCheckStorageService;
    }

    @Scheduled(cron = "0 0 6,18 * * *", zone = "Asia/Seoul")  // ✅ 하루 두 번 실행 (06:00, 18:00)
    public void fetchAndSaveFactChecks() {
        System.out.println("🕒 팩트체크 크롤링 실행: " + LocalDateTime.now());

        // 1. 팩트체크 데이터 가져오기
        var factChecks = factCheckCrawlerService.fetchFactChecks();

        // 2. MongoDB 저장
        factCheckStorageService.saveFactChecks(factChecks);
    }
}
