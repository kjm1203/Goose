package com.ssafy.irang.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.irang.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
