package com.ssafy.goose.domain.user.controller;

import com.ssafy.goose.domain.user.dto.UserResponseDto;
import com.ssafy.goose.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 모든 유저 조회
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // 특정 유저 조회
    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // 새로운 유저 생성
    @PostMapping
    public UserResponseDto createUser(@RequestParam String nickname, @RequestParam String password) {
        return userService.createUser(nickname, password);
    }
}
