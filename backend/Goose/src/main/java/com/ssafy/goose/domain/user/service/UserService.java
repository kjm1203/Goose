package com.ssafy.goose.domain.user.service;

import com.ssafy.goose.domain.user.dto.UserResponseDto;
import com.ssafy.goose.domain.user.entity.User;
import com.ssafy.goose.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 전체 유저 조회 (삭제되지 않은 유저)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findByIsDeletedFalse()
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 ID로 유저 조회
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDto::new)
                .orElse(null);
    }

    // 새로운 유저 추가
    public UserResponseDto createUser(String nickname, String password) {
        User user = User.builder()
                .nickname(nickname)
                .password(password)
                .createdAt((int) Instant.now().getEpochSecond()) // 현재 UNIX 타임스탬프 저장
                .isDeleted(false)
                .build();

        userRepository.save(user);
        return new UserResponseDto(user);
    }
}
