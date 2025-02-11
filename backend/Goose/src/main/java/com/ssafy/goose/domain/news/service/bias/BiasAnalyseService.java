package com.ssafy.goose.domain.news.service.bias;

import com.ssafy.goose.domain.news.entity.ReferenceNewsArticle;
import com.ssafy.goose.domain.news.repository.ReferenceNewsCustomRepository;
import com.ssafy.goose.domain.news.service.keyword.TitleKeywordExtractor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BiasAnalyseService {
    private final ReferenceNewsCustomRepository referenceNewsCustomRepository;
    private final AnalyseByTitle analyseByTitle;
    private final AnalyseByContent analyseByContent;
    private final TitleKeywordExtractor keywordExtractorService;

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

        // ✅ 4. 제목으로 분석 : FastAPI 서버로 NLP 검증 요청
        double bias_title = analyseByTitle.checkTitleWithReference(title, referenceContents);

        // ✅ 5. 내용으로 분석 : FastAPI 서버로 NLP 검증 요청
        double bias_content = analyseByContent.checkContentWithReference(content, referenceContents);

        return (bias_title + bias_content) / 2;
    }
}

