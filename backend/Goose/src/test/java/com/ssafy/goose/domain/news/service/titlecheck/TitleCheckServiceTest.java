package com.ssafy.goose.domain.news.service.titlecheck;

import com.ssafy.goose.domain.news.repository.ReferenceNewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class TitleCheckServiceTest {

    private ReferenceNewsRepository newsRepository;
    private TitleCheckClient titleCheckClient;
    private TitleCheckService titleCheckService;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        newsRepository = mock(ReferenceNewsRepository.class);
        titleCheckClient = mock(TitleCheckClient.class);

        // TitleCheckService에 Mock 주입
        titleCheckService = new TitleCheckService(newsRepository, titleCheckClient);
    }

    @Test
    void testAnalyzeTitleAgainstReferences() {
        // 🔹 3일 이내 뉴스 데이터 Mocking
//        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
//        List<NewsArticle> mockArticles = List.of(
//                new NewsArticle(1L, "트럼프, 중국과 관세전쟁 확전", "트럼프는 최근...", threeDaysAgo.plusDays(1)),
//                new NewsArticle(2L, "관세 정책 변화가 시장에 미치는 영향", "현재 관세 정책은...", threeDaysAgo.plusDays(2))
//        );
//        when(newsRepository.findRecentNews(threeDaysAgo)).thenReturn(mockArticles);
//
//        // 🔹 TitleCheckClient 응답 Mocking
//        when(titleCheckClient.checkTitleWithReference(anyString(), anyList()))
//                .thenReturn("Partially True"); // 예제 응답

        // ✅ 테스트 수행
        String testTitle = "트럼프 ‘관세전쟁’ 확전 우려에 코스피 약세";
        String result = titleCheckService.analyzeTitleAgainstReferences(testTitle);

        // 🔹 결과 출력 및 검증
        System.out.println("🔍 제목 검증 결과: " + result);
        assertNotNull(result);
    }
}
