package com.ssafy.goose.domain.news.dto;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsArticleDto {

    private String id;
    private String title;
    private String originalLink;
    private String naverLink;
    private String description;
    private String pubDate;
    private String content;
    private String topImage;
    private LocalDateTime extractedAt;

    public static NewsArticleDto fromEntity(NewsArticle article) {
        return NewsArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .originalLink(article.getOriginalLink())
                .naverLink(article.getNaverLink())
                .description(article.getDescription())
                .pubDate(article.getPubDate())
                .content(article.getContent())
                .topImage(article.getTopImage())
                .extractedAt(article.getExtractedAt())
                .build();
    }
}
