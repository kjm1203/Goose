package com.ssafy.goose;

import com.ssafy.goose.domain.news.scheduler.AutoCrawlingService;
import com.ssafy.goose.domain.factcheck.scheduler.AutoFactCheckCrawlingService;
import com.ssafy.goose.domain.news.service.titlecheck.TitleCheckService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 🔹 @Scheduled 활성화
public class GooseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GooseApplication.class, args);
	}

	// 🔹 앱 시작 후 즉시 뉴스 크롤링 실행 및 제목 검증 테스트
	@Bean
	public CommandLineRunner run(
			AutoCrawlingService autoCrawlingService,
			AutoFactCheckCrawlingService autoFactCheckCrawlingService,
			TitleCheckService titleCheckService
	) {
		return args -> {
			// ✅ 자동 뉴스 크롤링 (비활성화 가능)
//			 System.out.println("🚀 애플리케이션 실행 후 즉시 뉴스 크롤링 시작...");
//			 autoCrawlingService.fetchAndSaveTrendingNews();
//			 autoFactCheckCrawlingService.fetchAndStoreFactChecks();

			// ✅ 제목 검증 테스트 실행
			String testTitle = "1월 아파트 매매물량 46% 가격 낮췄다";  // 테스트할 기사 제목
			System.out.println("📝 제목 검증 테스트 시작: " + testTitle);

			double factCheckResult = titleCheckService.analyzeTitleAgainstReferences(testTitle);
			System.out.println("🔍 제목 검증 결과: " + factCheckResult);
		};
	}
}
