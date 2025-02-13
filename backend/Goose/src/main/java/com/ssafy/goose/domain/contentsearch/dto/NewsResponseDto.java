package com.ssafy.goose.domain.contentsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsResponseDto {

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

    // 객체 정보를 문자열로 반환
    @Override
    public String toString() {
        return "NewsResponseDto{" +
                "title='" + title + '\'' +
                ", originalLink='" + originalLink + '\'' +
                ", naverLink='" + naverLink + '\'' +
                ", description='" + description + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", paragraphs='" + paragraphs + '\'' +
                ", content='" + content + '\'' +
                ", topImage='" + topImage + '\'' +
                ", newsAgency='" + newsAgency + '\'' +
                ", extractedAt=" + extractedAt +
                '}';
    }
}
