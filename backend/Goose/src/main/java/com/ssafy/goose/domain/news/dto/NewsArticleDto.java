package com.ssafy.goose.domain.news.dto;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private List<String> paragraphs; // ✅ 뉴스 문단 정보 추가
    private List<Double> paragraphReliabilities; // 문단별 신뢰도 점수
    private List<String> paragraphReasons;       // 문단별 신뢰도 분석 결과(사유)
    private String topImage;
    private LocalDateTime extractedAt;
    private Double biasScore;      // ✅ 편향성 점수 필드
    private Double reliability;    // ✅ 기사 신뢰도 점수 필드 추가


    /**
     * 🔹 엔티티 → DTO 변환
     */
    public static NewsArticleDto fromEntity(NewsArticle article) {
        // extractedAt를 UTC로 가정하고, Asia/Seoul로 변환
        ZonedDateTime utcTime = article.getExtractedAt().atZone(ZoneId.of("UTC"));
        ZonedDateTime kstTime = utcTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        return NewsArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .originalLink(article.getOriginalLink())
                .naverLink(article.getNaverLink())
                .description(article.getDescription())
                .pubDate(article.getPubDate())
                .content(article.getContent())
                .paragraphs(article.getParagraphs())
                .topImage(article.getTopImage())
                .extractedAt(kstTime.toLocalDateTime()) // KST로 변환된 시간 저장
                .biasScore(article.getBiasScore() != null ? article.getBiasScore() : 0.0)
                .reliability(article.getReliability() != null ? article.getReliability() : 50.0)
                .paragraphReliabilities(article.getParagraphReliabilities())
                .paragraphReasons(article.getParagraphReasons())
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
                .paragraphs(this.paragraphs)
                .topImage(this.topImage)
                .extractedAt(this.extractedAt)
                .biasScore(this.biasScore != null ? this.biasScore : 0.0)
                .reliability(this.reliability != null ? this.reliability : 50.0) // ✅ 기본값 50.0 설정
                .paragraphReliabilities(this.paragraphReliabilities)
                .paragraphReasons(this.paragraphReasons)
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
            return zonedDateTime.toInstant().toEpochMilli();
        } catch (Exception e) {
            try {
                return ZonedDateTime.parse(this.pubDate).toInstant().toEpochMilli();
            } catch (Exception ex) {
                return -1;
            }
        }
    }
}
