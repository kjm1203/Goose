package com.ssafy.goose.domain.user.repository;

import com.ssafy.goose.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIsDeletedFalse(); // 삭제되지 않은 유저 조회
}
