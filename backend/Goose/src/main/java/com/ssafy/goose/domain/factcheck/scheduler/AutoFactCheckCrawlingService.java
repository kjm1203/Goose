package com.ssafy.goose.domain.factcheck.scheduler;

import com.ssafy.goose.domain.factcheck.crawling.FactCheckCrawlerService;
import com.ssafy.goose.domain.factcheck.model.FactCheck;
import com.ssafy.goose.domain.factcheck.storage.FactCheckStorageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoFactCheckCrawlingService {
    private final FactCheckCrawlerService factCheckCrawlerService;
    private final FactCheckStorageService factCheckStorageService;

    public AutoFactCheckCrawlingService(FactCheckCrawlerService factCheckCrawlerService, FactCheckStorageService factCheckStorageService) {
        this.factCheckCrawlerService = factCheckCrawlerService;
        this.factCheckStorageService = factCheckStorageService;
    }

//    @Scheduled(cron = "0 0 6,18 * * *", zone = "Asia/Seoul")  // ✅ 하루 2회 (06:00, 18:00)
    public void fetchAndStoreFactChecks() {
        System.out.println("🕒 FastAPI 팩트체크 크롤링 요청");

        List<FactCheck> factChecks = factCheckCrawlerService.fetchFactChecks();
        if (!factChecks.isEmpty()) {
            factCheckStorageService.saveFactChecks(factChecks);
            System.out.println("✅ 팩트체크 저장 완료: " + factChecks.size() + "개");
        } else {
            System.out.println("❌ 팩트체크 데이터를 가져오지 못했습니다.");
        }
    }
}
