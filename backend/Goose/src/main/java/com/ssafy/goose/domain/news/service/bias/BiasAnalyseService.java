package com.ssafy.goose.domain.news.service.bias;

import com.ssafy.goose.domain.news.entity.ReferenceNewsArticle;
import com.ssafy.goose.domain.news.repository.ReferenceNewsCustomRepository;
import com.ssafy.goose.domain.news.service.keyword.TitleKeywordExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class BiasAnalyseService {
    private final ReferenceNewsCustomRepository referenceNewsCustomRepository;
    private final AnalyseByTitle analyseByTitle;
    private final AnalyseByContent analyseByContent;
    private final TitleKeywordExtractor keywordExtractorService;
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ FastAPI 서버 URL (EC2 배포 시 주소 변경 필요)
    private static final String FASTAPI_URL = "http://localhost:5059/news/reliability";

    public BiasAnalyseService(
            ReferenceNewsCustomRepository referenceNewsCustomRepository,
            AnalyseByTitle analyseByTitle,
            AnalyseByContent analyseByContent,
            TitleKeywordExtractor keywordExtractorService) {
        this.referenceNewsCustomRepository = referenceNewsCustomRepository;
        this.analyseByTitle = analyseByTitle;
        this.analyseByContent = analyseByContent;
        this.keywordExtractorService = keywordExtractorService;
    }

    public double analyzeBias(String title, String content) {
        // ✅ 1. 제목에서 주요 키워드 3개 추출
        List<String> keywords = keywordExtractorService.extractTopKeywords(title, 3);
        System.out.println("🔹 추출된 키워드: " + keywords);

        // ✅ 2. 3일 이내 키워드 기반 뉴스 검색
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<ReferenceNewsArticle> recentArticles = referenceNewsCustomRepository.findNewsByKeywords(keywords, threeDaysAgo);

        if (recentArticles.isEmpty()) {
            System.out.println("❌ 해당 키워드와 관련된 최근 뉴스 없음");
            return 50.0;
        }

        // ✅ 3. 검색된 뉴스 기사들의 본문 내용 추출
        List<String> referenceContents = recentArticles.stream()
                .map(ReferenceNewsArticle::getContent)
                .collect(Collectors.toList());

        if (referenceContents.size() < 2) {
            System.out.println("❌ 비교할 레퍼런스 뉴스 문단이 부족함");
            return 50.0;  // 신뢰성 중간값 반환
        }

        // ✅ 4. 제목으로 분석 : FastAPI 서버로 NLP 검증 요청
        double bias_title = analyseByTitle.checkTitleWithReference(title, referenceContents);

        // ✅ 5. 내용으로 분석 : FastAPI 서버로 NLP 검증 요청
        double bias_content = analyseByContent.checkContentWithReference(content, referenceContents);

        // ✅ 6. 문단 신뢰성 분석 요청 (FastAPI 호출) - 첫 번째 문단과 나머지 문단 전달
        double paragraph_reliability = analyzeParagraphReliability(recentArticles.get(0), recentArticles.subList(1, recentArticles.size()));

        // ✅ 최종 신뢰성 점수 계산
        return (bias_title + bias_content + paragraph_reliability) / 3;
    }

    private double analyzeParagraphReliability(ReferenceNewsArticle firstArticle, List<ReferenceNewsArticle> remainingArticles) {
        // ✅ FastAPI 요청 데이터 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("news", Map.of(
                "title", firstArticle.getTitle(),
                "paragraphs", firstArticle.getParagraphs()
        ));
        requestBody.put("references", remainingArticles.stream().map(article -> Map.of(
                "title", article.getTitle(),
                "paragraphs", article.getParagraphs()
        )).collect(Collectors.toList()));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(FASTAPI_URL, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Double> reliabilityScores = (List<Double>) response.getBody().get("paragraph_reliability_scores");
                List<String> bestMatches = (List<String>) response.getBody().get("best_evidence_paragraphs");

                // ✅ best_matches를 콘솔에 출력
                System.out.println("✅ FastAPI 신뢰성 분석 결과:");
                for (int i = 0; i < bestMatches.size(); i++) {
                    System.out.println("🔹 문단 " + (i + 1) + " 신뢰성 점수: " + reliabilityScores.get(i));
                    System.out.println(firstArticle.getParagraphs().get(i));
                    System.out.println("   ➜ 가장 유사한 문단: " + bestMatches.get(i));
                }

                return reliabilityScores.stream().mapToDouble(Double::doubleValue).average().orElse(50.0);
            }
        } catch (Exception e) {
            System.err.println("❌ FastAPI 요청 실패: " + e.getMessage());
        }

        return 50.0;  // 오류 발생 시 중간값 반환
    }
}
