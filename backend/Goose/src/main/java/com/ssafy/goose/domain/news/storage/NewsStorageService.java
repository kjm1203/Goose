package com.ssafy.goose.domain.news.storage;

import com.ssafy.goose.domain.news.crawling.NewsContentScraping;
import com.ssafy.goose.domain.news.crawling.NewsParagraphSplitService;
import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.repository.NewsRepository;
import com.ssafy.goose.domain.news.analysis.NewsBiasAnalysisService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsStorageService {
    private final NewsRepository newsRepository;
    private final NewsBiasAnalysisService newsBiasAnalysisService;
    private final NewsParagraphSplitService newsParagraphSplitService;
    private final NewsContentScraping newsContentScraping; // ✅ FastAPI 뉴스 크롤링 서비스 추가

    public NewsStorageService(NewsRepository newsRepository,
                              NewsBiasAnalysisService newsBiasAnalysisService,
                              NewsParagraphSplitService newsParagraphSplitService,
                              NewsContentScraping newsContentScraping) {
        this.newsRepository = newsRepository;
        this.newsBiasAnalysisService = newsBiasAnalysisService;
        this.newsParagraphSplitService = newsParagraphSplitService;
        this.newsContentScraping = newsContentScraping;
    }

    public void saveNewsToMongoDB(Map<String, Object> newsData, String keyword) {
        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

        for (Map<String, Object> item : newsItems) {
            String link = (String) item.get("link"); // ✅ 뉴스 기사 URL 가져오기
            if (link == null || link.isEmpty()) {
                System.out.println("❌ [NewsStorageService] 링크 없음: " + item.get("title"));
                continue;
            }

            // ✅ FastAPI를 이용해 뉴스 본문과 대표 이미지 가져오기
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

            // ✅ 문단 분리 수행 (FastAPI)
            List<String> paragraphs = newsParagraphSplitService.getSplitParagraphs(content);

            // ✅ 기존 같은 키워드 뉴스 가져오기
            List<NewsArticle> relatedArticles = newsRepository.findByTitleRegex(keyword);
            List<String> existingContents = relatedArticles.stream()
                    .map(NewsArticle::getContent)
                    .collect(Collectors.toList());

            // ✅ 편향성 분석
            Double biasScore = newsBiasAnalysisService.getBiasScore(existingContents, content, keyword);

            // ✅ MongoDB에 저장
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
                    .build();

            newsRepository.save(article);
        }
    }
}
