package com.ssafy.goose.domain.contentsearch.service;

import com.ssafy.goose.domain.contentsearch.dto.KeywordResponseDto;
import com.ssafy.goose.domain.contentsearch.dto.NewsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {

    private final KeywordService keywordService;
    private final SearchService searchService;

    @Autowired
    public ContentService(KeywordService keywordService, SearchService searchService) {
        this.keywordService = keywordService;
        this.searchService = searchService;
    }

    // 텍스트에서 키워드 추출 후 키워드 기반 뉴스 검색
    public List<NewsResponseDto> processKeywordAndSearch(String text) {
        // 키워드 추출
        KeywordResponseDto keywordResponse = keywordService.extractKeywords(text);
        String[] keywords = keywordResponse.getKeywords();

        // 키워드 기반 뉴스 검색
        return searchService.searchNewsByKeyword(keywords);
    }
}
