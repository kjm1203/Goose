package com.ssafy.goose.domain.news.crawling.controller;

import com.ssafy.goose.domain.news.crawling.model.NewsArticle;
import com.ssafy.goose.domain.news.crawling.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    // 특정 날짜의 기사 조회 API
    @GetMapping("/date")
    public List<NewsArticle> getArticlesByDate(@RequestParam String date) {
        return newsService.getArticlesByDate(date);
    }

    // 특정 날짜 범위 기사 조회 API
    @GetMapping("/date-range")
    public List<NewsArticle> getArticlesBetweenDates(@RequestParam String start, @RequestParam String end) {
        return newsService.getArticlesBetweenDates(start, end);
    }

    // 특정 키워드를 포함하는 기사 조회 API
    @GetMapping("/search")
    public List<NewsArticle> getArticlesByKeyword(@RequestParam String keyword) {
        return newsService.getArticlesByKeyword(keyword);
    }

    // 새로운 기사 저장 API
    @PostMapping("/save")
    public String saveArticle(@RequestBody NewsArticle article) {
        newsService.saveNewsArticle(article);
        return "News article saved successfully!";
    }
}
