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
                .token(null)
                .build();

        userRepository.save(user);
        return UserResponseDto.success();
    }

    // 로그인
    public UserResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);

        if (user == null) {
            return UserResponseDto.error("존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return UserResponseDto.error("비밀번호가 일치하지 않습니다.");
        }

        // AccessToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());

        // 기존 RefreshToken이 있으면 유지, 없으면 새로 생성
        String refreshToken = user.getToken();
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());
            user.setToken(refreshToken);
            userRepository.save(user);
        }

        return UserResponseDto.success(accessToken, refreshToken);
    }

    // 로그아웃
    public UserResponseDto logout(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return UserResponseDto.error("유효하지 않은 RefreshToken입니다.");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // RefreshToken 삭제 (DB에서 무효화)
        user.setToken(null);
        userRepository.save(user);

        return UserResponseDto.success();
    }
    
    // 회원 가입 시 ID 중복 체크
    public UserCheckResponseDto checkUsernameAvailability(String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        return exists ? new UserCheckResponseDto(false, "이미 사용 중인 ID입니다.")
                : new UserCheckResponseDto(true, "사용 가능한 ID입니다.");
    }

    // 회원 가입 시 닉네임 중복 체크
    public UserCheckResponseDto checkNicknameAvailability(String nickname) {
        boolean exists = userRepository.findByNickname(nickname).isPresent();
        return exists ? new UserCheckResponseDto(false, "이미 사용 중인 닉네임입니다.")
                : new UserCheckResponseDto(true, "사용 가능한 닉네임입니다.");
    }

    // RefreshToken 검증 및 AccessToken 재발급
    public UserResponseDto refreshAccessToken(String refreshToken) {
        // RefreshToken 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return UserResponseDto.error("유효하지 않은 RefreshToken입니다.");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 요청된 RefreshToken과 DB에 저장된 RefreshToken이 같은지 확인
        if (!refreshToken.equals(user.getToken())) {
            return UserResponseDto.error("RefreshToken이 일치하지 않습니다.");
        }

        // RefreshToken을 즉시 폐기 및 새로 발급
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);
        user.setToken(newRefreshToken);
        userRepository.save(user);

        // 새로운 AccessToken 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(username);

        return UserResponseDto.success(newAccessToken, newRefreshToken);
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
