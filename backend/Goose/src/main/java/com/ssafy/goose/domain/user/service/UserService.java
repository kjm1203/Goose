package com.ssafy.goose.domain.user.service;

import com.ssafy.goose.domain.news.entity.NewsArticle;
import com.ssafy.goose.domain.news.repository.NewsRepository;
import com.ssafy.goose.domain.user.dto.*;
import com.ssafy.goose.domain.user.entity.User;
import com.ssafy.goose.domain.user.repository.UserNewsDeterminationRepository;
import com.ssafy.goose.domain.user.repository.UserRepository;
import com.ssafy.goose.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserNewsDeterminationRepository determinationRepository;
    private final NewsRepository newsRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       UserNewsDeterminationRepository determinationRepository,
                       NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.determinationRepository = determinationRepository;
        this.newsRepository = newsRepository;
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

    public UserCheckResponseDto checkUsernameAvailability(String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        return exists ? new UserCheckResponseDto(false, "이미 사용 중인 ID입니다.")
                : new UserCheckResponseDto(true, "사용 가능한 ID입니다.");
    }

    public UserCheckResponseDto checkNicknameAvailability(String nickname) {
        boolean exists = userRepository.findByNickname(nickname).isPresent();
        return exists ? new UserCheckResponseDto(false, "이미 사용 중인 닉네임입니다.")
                : new UserCheckResponseDto(true, "사용 가능한 닉네임입니다.");
    }


    //읽은 뉴스 조회
    public List<NewsDeterminationResponseDto> getNewsDeterminations(Long userId, String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        return determinationRepository.findTop10ByUserIdOrderByDeterminedAtDesc(userId)
                .stream()
                .map(determination -> {
                    NewsArticle newsArticle = newsRepository.findById(determination.getNewsId())
                            .orElseThrow(() -> new IllegalStateException("뉴스를 찾을 수 없습니다."));

                    return NewsDeterminationResponseDto.builder()
                            .newsId(determination.getNewsId())
                            .newsTitle(newsArticle.getTitle())
                            .newsContent(newsArticle.getContent())
                            .searchType(determination.getSearchType())
                            .determinedAt(determination.getDeterminedAt())
                            .reliability(newsArticle.getReliability())
                            .build();
                })
                .toList();
    }
}
