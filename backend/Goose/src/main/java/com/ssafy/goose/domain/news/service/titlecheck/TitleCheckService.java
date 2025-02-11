package com.ssafy.goose.domain.news.service.titlecheck;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.repository.ReferenceNewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TitleCheckService {
    private final ReferenceNewsRepository referenceNewsRepositorynewsRepository;
    private final TitleCheckClient titleCheckClient;

    public TitleCheckService(ReferenceNewsRepository referenceNewsRepositorynewsRepository, TitleCheckClient titleCheckClient) {
        this.referenceNewsRepositorynewsRepository = referenceNewsRepositorynewsRepository;
        this.titleCheckClient = titleCheckClient;
    }

    public String analyzeTitleAgainstReferences(String title) {
        // 🔹 주요 키워드 3개 추출
        

        // 🔹 주요 키워드 3개로 레퍼런스 뉴스 검색
        List<NewsArticle> recentArticles = referenceNewsRepositorynewsRepository.();

        // 🔹 Reference News의 본문만 추출
        List<String> referenceContents = recentArticles.stream()
                .map(NewsArticle::getContent)
                .collect(Collectors.toList());

        // 🔹 FastAPI 서버로 NLP 검증 요청
        return titleCheckClient.checkTitleWithReference(title, referenceContents);
    }
}
