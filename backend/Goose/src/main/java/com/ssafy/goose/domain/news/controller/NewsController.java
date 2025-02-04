package com.ssafy.goose.domain.news.controller;

import com.ssafy.goose.domain.news.dto.NewsArticleDto;
import com.ssafy.goose.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // 뉴스 업로드
    @PostMapping("/upload")
    public ResponseEntity<NewsArticleDto> uploadNews(@RequestBody NewsArticleDto newsDto) {
        return ResponseEntity.ok(newsService.uploadNews(newsDto));
    }

    // 뉴스 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<NewsArticleDto>> getNewsList() {
        return ResponseEntity.ok(newsService.getNewsList());
    }

    // 뉴스 검색 (제목 기반)
    @GetMapping("/search")
    public ResponseEntity<List<NewsArticleDto>> searchNews(@RequestParam String keyword) {
        return ResponseEntity.ok(newsService.searchNews(keyword));
    }

    // 뉴스 상세 조회
    @GetMapping("/{newsId}")
    public ResponseEntity<NewsArticleDto> getNewsById(@PathVariable String newsId) {
        Optional<NewsArticleDto> news = newsService.getNewsById(newsId);
        return news.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
