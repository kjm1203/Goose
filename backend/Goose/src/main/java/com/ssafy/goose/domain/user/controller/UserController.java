package com.ssafy.goose.domain.user.controller;

import com.ssafy.goose.domain.user.dto.LoginRequestDto;
import com.ssafy.goose.domain.user.dto.NewsDeterminationResponseDto;
import com.ssafy.goose.domain.user.dto.SignupRequestDto;
import com.ssafy.goose.domain.user.dto.UserResponseDto;
import com.ssafy.goose.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API", description = "회원가입, 로그인, 로그아웃 기능을 제공하는 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<UserResponseDto> signup(@RequestBody SignupRequestDto signupRequest) {
        return ResponseEntity.ok(userService.signup(signupRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다.")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    public ResponseEntity<UserResponseDto> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return ResponseEntity.ok(userService.logout(token));
    }

    //읽은 뉴스 조회 기능 추가
    @GetMapping("/{userId}/determinations")
    @Operation(summary = "뉴스 판별 기록 조회", description = "사용자의 최근 10개 뉴스 판별 기록을 조회합니다.")
    public ResponseEntity<List<NewsDeterminationResponseDto>> getDeterminations(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return ResponseEntity.ok(userService.getNewsDeterminations(userId, token));
    }
}
