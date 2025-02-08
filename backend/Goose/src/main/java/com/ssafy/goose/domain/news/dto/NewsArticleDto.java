package com.ssafy.goose.domain.news.dto;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
    private List<String> paragraphs; // ✅ 누락된 필드 추가 (전문으로부터 분리된 문단들)
    private String topImage;
    private LocalDateTime extractedAt;
    private Double biasScore; // ✅ 편향성 점수 필드

    /**
     * 🔹 엔티티 → DTO 변환
     */
    public static NewsArticleDto fromEntity(NewsArticle article) {
        return NewsArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .originalLink(article.getOriginalLink())
                .naverLink(article.getNaverLink())
                .description(article.getDescription())
                .pubDate(article.getPubDate())
                .content(article.getContent())
                .paragraphs(article.getParagraphs()) // ✅ 문단 정보 추가
                .topImage(article.getTopImage())
                .extractedAt(article.getExtractedAt())
                .biasScore(article.getBiasScore() != null ? article.getBiasScore() : 0.0) // ✅ 기본값 0.0 설정
                .build();
    }

    /**
     * 🔹 DTO → 엔티티 변환
     */
    public NewsArticle toEntity() {
        return NewsArticle.builder()
                .id(this.id)
                .title(this.title)
                .originalLink(this.originalLink)
                .naverLink(this.naverLink)
                .description(this.description)
                .pubDate(this.pubDate)
                .content(this.content)
                .paragraphs(this.paragraphs) // ✅ 문단 정보 추가
                .topImage(this.topImage)
                .extractedAt(this.extractedAt)
                .biasScore(this.biasScore != null ? this.biasScore : 0.0) // ✅ 기본값 0.0 설정
                .build();
    }

    /**
     * 🔹 날짜 변환 메서드 개선 (ISO 8601, RFC 1123 형식 지원)
     */
    public long getPubDateTimestamp() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH
            );
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(this.pubDate, formatter);
            return zonedDateTime.toInstant().toEpochMilli(); // ✅ Timestamp 변환
        } catch (Exception e) {
            try {
                // ✅ ISO 8601 날짜 형식 지원
                return ZonedDateTime.parse(this.pubDate).toInstant().toEpochMilli();
            } catch (Exception ex) {
                return -1; // 변환 실패 시 -1 반환
            }
        }
    }
}
