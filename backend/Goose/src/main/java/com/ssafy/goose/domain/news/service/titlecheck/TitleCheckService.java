package com.ssafy.goose.domain.news.service.titlecheck;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.repository.ReferenceNewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TitleCheckService {
    private final ReferenceNewsRepository newsRepository;
    private final TitleCheckClient titleCheckClient;

    public TitleCheckService(ReferenceNewsRepository newsRepository, TitleCheckClient titleCheckClient) {
        this.newsRepository = newsRepository;
        this.titleCheckClient = titleCheckClient;
    }

    public String analyzeTitleAgainstReferences(String title) {
        // 🔹 3일 이내 Reference News 검색
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<NewsArticle> recentArticles = newsRepository.findRecentNews(threeDaysAgo);

        // 🔹 Reference News의 본문만 추출
        List<String> referenceContents = recentArticles.stream()
                .map(NewsArticle::getContent)
                .collect(Collectors.toList());

        // 🔹 FastAPI 서버로 NLP 검증 요청
        return titleCheckClient.checkTitleWithReference(title, referenceContents);
    }
}
