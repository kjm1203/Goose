package com.ssafy.goose.domain.contentsearch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // 뉴스 고유 ID
    private String title;              // 제목
    private String originalLink;       // 원본 링크
    private String naverLink;          // 네이버 링크
    private String description;        // 요약
    private String pubDate;            // 게시 날짜
    private String paragraphs;         // 뉴스 본문에 포함된 주요 문단들
    private String content;            // 본문 내용
    private String topImage;           // 대표 이미지 URL
    private String newsAgency;         // 언론사
    private LocalDateTime extractedAt; // 추출 시간
}
