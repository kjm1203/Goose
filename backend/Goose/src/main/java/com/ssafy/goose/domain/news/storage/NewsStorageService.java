package com.ssafy.goose.domain.news.storage;

import com.ssafy.goose.domain.news.service.bias.BiasAnalyseService;
import com.ssafy.goose.domain.news.service.crawling.NewsContentScraping;
import com.ssafy.goose.domain.news.service.paragraph.NewsParagraphSplitService;
import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.entity.ReferenceNewsArticle;
import com.ssafy.goose.domain.news.repository.NewsRepository;
import com.ssafy.goose.domain.news.repository.ReferenceNewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsStorageService {
    private final NewsRepository newsRepository;
    private final ReferenceNewsRepository referenceNewsRepository;

    private final NewsParagraphSplitService newsParagraphSplitService;
    private final NewsContentScraping newsContentScraping; // ✅ FastAPI 뉴스 크롤링 서비스 추가
    private final BiasAnalyseService biasAnalyseService;

    public NewsStorageService(NewsRepository newsRepository,
                              ReferenceNewsRepository referenceNewsRepository,
                              NewsParagraphSplitService newsParagraphSplitService,
                              NewsContentScraping newsContentScraping,
                              BiasAnalyseService biasAnalyseService) {
        this.newsRepository = newsRepository;
        this.referenceNewsRepository = referenceNewsRepository;
        this.newsParagraphSplitService = newsParagraphSplitService;
        this.newsContentScraping = newsContentScraping;
        this.biasAnalyseService =  biasAnalyseService;
    }

    // 주요 기능
    public void saveNewsToMongoDB(Map<String, Object> newsData, String keyword) {
        // 1. 키워드로 뉴스 검색해서 가져오기 (AutoCrawlingService로부터 saveNewsToMongoDB로 넘어옴)
        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

        for (Map<String, Object> item : newsItems) {
            // 2. 뉴스데이터에서 기사 링크 추출
            String link = (String) item.get("link"); // ✅ 뉴스 기사 URL 가져오기
            if (link == null || link.isEmpty()) {
                System.out.println("❌ [NewsStorageService] 링크 없음: " + item.get("title"));
                continue;
            }

            // ✅ 3. FastAPI를 이용해 뉴스 본문과 대표 이미지 가져오기
            Map<String, Object> scrapedData = newsContentScraping.extractArticle(link);
            if (scrapedData == null || !scrapedData.containsKey("text")) {
                System.out.println("❌ [NewsStorageService] 본문 크롤링 실패: " + item.get("title"));
                continue;
            }

            String cleanTitle = (String) scrapedData.get("title");
            String content = (String) scrapedData.get("text");
            String topImage = (String) scrapedData.get("image"); // ✅ 대표 이미지

            if (content.length() < 100) {
                System.out.println("❌ [NewsStorageService] 본문이 너무 짧아서 제외: " + item.get("title"));
                continue;
            }

            // ✅ 4. 문단 분리 수행 (FastAPI)
            List<String> paragraphs = newsParagraphSplitService.getSplitParagraphs(content);

            // ✅ 5. 편향성 분석
            Double biasScore = biasAnalyseService.analyzeBias(cleanTitle, content);

            // AI 가짜뉴스 여부 판별 (민성이 모델)

            // 언론사 신뢰도 판정 (추후 추가)

            // ✅ 6. MongoDB에 저장
            NewsArticle article = NewsArticle.builder()
                    .title(cleanTitle)
                    .originalLink((String) item.get("originallink"))
                    .naverLink(link)
                    .description((String) item.get("description"))
                    .pubDate((String) item.get("pubDate"))
                    .content(content)
                    .paragraphs(paragraphs)
                    .topImage(topImage) // ✅ 크롤링된 대표 이미지 저장
                    .extractedAt(LocalDateTime.now())
                    .biasScore(biasScore)
                    .reliability(100.0) // ✅ 임시 신뢰도 100
                    .build();

            newsRepository.save(article);
        }
    }

    // 단순 참고용 뉴스들을 저장하는 메서드
    public void saveReferenceNewsToMongoDB(Map<String, Object> newsData, String keyword) {
        // 1. 키워드로 뉴스 검색해서 가져오기 (AutoCrawlingService로부터 saveNewsToMongoDB로 넘어옴)
        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

        // 링크 테스트
        for (Map<String, Object> item : newsItems) {
            String link = (String) item.get("link");
            System.out.println("뉴스 검색 API가 가져온 링크 : "+link);
        }

        for (Map<String, Object> item : newsItems) {
            // 2. 뉴스데이터에서 기사 링크 추출
            String link = (String) item.get("link");
            if (link == null || link.isEmpty()) continue;

            // ✅ 3. FastAPI를 이용해 뉴스 본문과 대표 이미지 가져오기
            Map<String, Object> scrapedData = newsContentScraping.extractArticle(link);
            if (scrapedData == null || !scrapedData.containsKey("text")) continue;

            String cleanTitle = (String) scrapedData.get("title");
            String content = (String) scrapedData.get("text");
            String topImage = (String) scrapedData.get("image");

            if (content.length() < 100) continue;

            // ✅ 4. 문단 분리 수행 (FastAPI)
            List<String> paragraphs = newsParagraphSplitService.getSplitParagraphs(content);

            ReferenceNewsArticle article = ReferenceNewsArticle.builder()
                    .title(cleanTitle)
                    .originalLink((String) item.get("originallink"))
                    .naverLink(link)
                    .description((String) item.get("description"))
                    .pubDate((String) item.get("pubDate"))
                    .content(content)
                    .paragraphs(paragraphs)
                    .topImage(topImage)
                    .extractedAt(LocalDateTime.now())
                    .build();

            referenceNewsRepository.save(article);
        }
    }
}
