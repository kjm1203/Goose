package com.ssafy.goose.domain.news.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "news_articles") // MongoDB 컬렉션 이름 지정
public class NewsArticleDto {

    // Getters and Setters
    @Id
    private String id; // MongoDB 기본 키 (_id)

    private String title;         // 뉴스 제목
    private String originalLink;  // 원본 기사 링크
    private String naverLink;     // 네이버 기사 링크
    private String description;   // 기사 요약
    private String pubDate;       // 발행 날짜 (ISO-8601 형식)
    private String content;       // 본문 (goose3로 추출)
    private String topImage;      // 대표 이미지 URL
    private LocalDateTime extractedAt; // 데이터 크롤링 시간

    public NewsArticleDto(String title, String originalLink, String naverLink, String description, String pubDate,
                          String content, String topImage, LocalDateTime extractedAt) {
        this.title = title;
        this.originalLink = originalLink;
        this.naverLink = naverLink;
        this.description = description;
        this.pubDate = pubDate;
        this.content = content;
        this.topImage = topImage;
        this.extractedAt = extractedAt;
    }
}
