package com.ssafy.goose.domain.user.dto;

import com.ssafy.goose.domain.user.entity.User;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class UserResponseDto {
    private Long id;
    private String nickname;
    private LocalDateTime createdAt; // 변환된 날짜 반환

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.createdAt = convertToLocalDateTime(user.getCreatedAt()); // UNIX timestamp -> LocalDateTime 변환
    }

    private LocalDateTime convertToLocalDateTime(int timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("Asia/Seoul"));
    }
}
