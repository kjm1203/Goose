package com.ssafy.goose.domain.user.service;

import com.ssafy.goose.domain.user.dto.LoginRequestDto;
import com.ssafy.goose.domain.user.dto.UserResponseDto;
import com.ssafy.goose.domain.user.dto.SignupRequestDto;
import com.ssafy.goose.domain.user.entity.User;
import com.ssafy.goose.domain.user.repository.UserRepository;
import com.ssafy.goose.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입
    public UserResponseDto signup(SignupRequestDto signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return UserResponseDto.error("이미 존재하는 username 입니다.");
        }
        if (userRepository.findByNickname(signupRequest.getNickname()).isPresent()) {
            return UserResponseDto.error("이미 존재하는 nickname 입니다.");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .nickname(signupRequest.getNickname())
                .createdAt((int) Instant.now().getEpochSecond())
                .isDeleted(false)
                .build();

        userRepository.save(user);
        return UserResponseDto.success();
    }

    // 로그인
    public UserResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);

        if (user == null) {
            return UserResponseDto.error("사용자가 존재하지 않습니다.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return UserResponseDto.error("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getUsername());
        return UserResponseDto.success(token);
    }

    // 로그아웃
    public UserResponseDto logout(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return UserResponseDto.error("유효하지 않은 토큰입니다.");
        }
        return UserResponseDto.success();
    }
}
