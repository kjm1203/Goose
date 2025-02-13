package com.ssafy.goose;

import com.ssafy.goose.domain.news.scheduler.AutoCrawlingService;
import com.ssafy.goose.domain.factcheck.scheduler.AutoFactCheckCrawlingService;
import com.ssafy.goose.domain.news.service.bias.BiasAnalyseService;
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
			BiasAnalyseService biasAnalyseService
	) {
		return args -> {
			// ✅ 자동 뉴스 크롤링 (비활성화 가능)
//			 System.out.println("🚀 애플리케이션 실행 후 즉시 뉴스 크롤링 시작...");
			 autoCrawlingService.fetchAndSaveTrendingNews();
//			 autoFactCheckCrawlingService.fetchAndStoreFactChecks();

			// ✅ 제목 검증 테스트 실행
			String testTitle = "";  // 테스트할 기사 제목
			String testContent = """
					파월 “금리인하 서두르지 않을 것” 발언에…
					예상보다 강한 CPI 나오자…“바이든 탓”
					[뉴욕=이데일리 김상윤 특파원] 도널드 트럼프 대통령이 12일(현지시간) 금리를 인하할 필요가 있다며 제롬 파월 연방준비제도 의장을 다시 한번 압박했다.
					그는 이날 자신의 소셜미디어 트루스소셜에서 “금리를 낮춰야 한다, 이는 다가오는 관세(인상)과 함께 진행돼야 한다!!! 락앤롤, 미국!!!”이라는 글을 올렸다.
					이 발언은파월 연준 의장이 상원 은행위원회 청문회에서 인플레이션의 진행 상황을 지켜보면서 금리를 낮추기 위해 “서두를 필요가 없다”고 말한 지 하루 만에 나왔다.
					트럼프 대통령은 연준의 금리결정에 지속적으로 개입할 의사를 내비치고 있다. 취임 직후 스위스 다보스에서 열린 세계경제포럼에서 트럼프 대통령은 연준에 대한 직접적인 권한이 없음에도 불구하고 ‘연준에게 금리인하를 요구하겠다’고 밝힌 바 있다. 연준이 1월 금리를 동결하자 그는 즉각 맹비난에 나서기도 했다. 트럼프 대통령은 “제롬 파월 연방준비제도 의장과 연준이 인플레이션으로 만든 문제를 막는 데 실패했다”며 “은행 규제에 대해서도 끔찍한 일을 해 왔다”고 글을 올리기도 했다. 그러다 며칠후 연준이 금리를 동결한 것은 옳은 결정이었다고 하면서 말을 뒤바꾸기도 했지만, 여전히 연준의 금리인하를 압박하는 모양새다.
					트럼프의 게시물이 올라온 지 약 30분 후, 노동통계국은 1월 소비자 물가가 예상보다 더 많이 상승했다고 발표했다. 미 노동통계국에 따르면 1월 소비자물가지수(CPI)는 전월대비 0.5%, 전년동기대비 3.0% 상승했다. 이는 다우존스 예상치 (0.3%, 2.9%)를 웃돈 수치다.
					변동성이 큰 식품과 에너지를 제외한 근원 CPI는 전월대비 0.4%, 전년동기대비 3.3% 상승했다. 월가 컨세서스는 0.3%, 전년비 3.2%이었는데 이를 소폭 웃돈 것이다.
					인플레이션이 연방준비제도의 목표치 2%를 향해 지속적으로 전진하기 보다는 다시 후퇴하는 모습이 나타난 것이다. 견고한 고용시장과 함께 인플레이션 둔화세가 계속 멈춘 상황에서 연방준비제도는 금리 인하에 더욱 신중론을 펼칠 가능성이 크다.
					CPI가 나온 이후 트럼프 대통령은 소셜미디어에 재차 “바이든이 인플레이션 올렸다”라는 글을 올렸다. 인플레이션 문제를 전임 대통령인 조 바이든에게 돌린 것이다.
					""";  // 테스트할 기사 제목

//			System.out.println("📝 Bias 검증 테스트 시작: ");
//
//			double biasScore = biasAnalyseService.analyzeBias(testTitle, testContent);
//			System.out.println("🔍 편향성 검증 결과: " + biasScore);
		};
	}
}
