package com.ssafy.goose.domain.news.storage;

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

    public NewsStorageService(NewsRepository newsRepository, NewsBiasAnalysisService newsBiasAnalysisService, NewsParagraphSplitService newsParagraphSplitService) {
        this.newsRepository = newsRepository;
        this.newsBiasAnalysisService = newsBiasAnalysisService;
        this.newsParagraphSplitService = newsParagraphSplitService;
    }

    public void saveNewsToMongoDB(Map<String, Object> newsData, String keyword) {
        List<Map<String, Object>> newsItems = (List<Map<String, Object>>) newsData.get("items");

        for (Map<String, Object> item : newsItems) {
            String content = (String) item.get("text"); // 본문 데이터
            if (content == null || content.length() < 100) {
                continue; // 본문이 너무 짧으면 제외
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

            NewsArticle article = NewsArticle.builder()
                    .title((String) item.get("title"))
                    .originalLink((String) item.get("originallink"))
                    .naverLink((String) item.get("link"))
                    .description((String) item.get("description"))
                    .pubDate((String) item.get("pubDate"))
                    .content(content)
                    .paragraphs(paragraphs)
                    .topImage(null) // 이미지 처리 필요 시 추가
                    .extractedAt(LocalDateTime.now())
                    .biasScore(biasScore)
                    .build();

            newsRepository.save(article);
        }
    }
}
