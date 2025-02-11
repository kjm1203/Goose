package com.ssafy.goose.domain.contentsearch.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.goose.domain.contentsearch.entity.News;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentNewsRepository extends JpaRepository<News, Long> {

    // keyword가 뉴스 title, description, content에 있으면 검색
    @Query("SELECT n FROM News n WHERE n.title LIKE %:keyword% OR n.description LIKE %:keyword% OR n.content LIKE %:keyword%")
    List<News> searchByTitleOrDescriptionOrContent(@Param("keyword") String keyword);
}
