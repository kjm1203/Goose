// UserNewsDeterminationRepository.java
package com.ssafy.goose.domain.user.repository;

import com.ssafy.goose.domain.user.entity.UserNewsDetermination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserNewsDeterminationRepository extends JpaRepository<UserNewsDetermination, Long> {
    List<UserNewsDetermination> findTop10ByUserIdOrderByDeterminedAtDesc(Long userId);
}