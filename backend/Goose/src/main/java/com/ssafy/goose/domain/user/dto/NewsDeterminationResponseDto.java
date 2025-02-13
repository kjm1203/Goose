// NewsDeterminationResponseDto.java
package com.ssafy.goose.domain.user.dto;

import lombok.*;

@Getter
@Builder
public class NewsDeterminationResponseDto {
    private String newsId;
    private String newsTitle;
    private String newsContent;
    private String searchType;
    private int determinedAt;
    private Double reliability;
}