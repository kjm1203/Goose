package com.ssafy.goose.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ null 값은 JSON 응답에서 제외
public class UserResponseDto {
    private boolean success;
    private String accessToken;
    private String error;

    // 로그인 성공 응답 (accessToken 포함)
    public static UserResponseDto success(String accessToken) {
        return new UserResponseDto(true, accessToken, null);
    }

    // 회원가입, 로그아웃 성공 응답 (토큰 필요 없음)
    public static UserResponseDto success() {
        return new UserResponseDto(true, null, null);
    }

    // 실패 응답 (에러 메시지 포함)
    public static UserResponseDto error(String errorMessage) {
        return new UserResponseDto(false, null, errorMessage);
    }
}
