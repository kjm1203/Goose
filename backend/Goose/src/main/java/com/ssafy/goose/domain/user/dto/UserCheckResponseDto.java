package com.ssafy.goose.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCheckResponseDto {
    private boolean available;  // true: 사용 가능, false: 중복됨
    private String message;
}
