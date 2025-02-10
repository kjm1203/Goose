package com.ssafy.goose.domain.factcheck.controller;

import com.ssafy.goose.domain.factcheck.model.FactCheck;
import com.ssafy.goose.domain.factcheck.repository.FactCheckRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factcheck")
public class FactCheckController {
    private final FactCheckRepository factCheckRepository;

    public FactCheckController(FactCheckRepository factCheckRepository) {
        this.factCheckRepository = factCheckRepository;
    }

    // ✅ 모든 팩트체크 조회
    @GetMapping("/all")
    public List<FactCheck> getAllFactChecks() {
        return factCheckRepository.findAll();
    }

    // ✅ 특정 키워드가 포함된 팩트체크 검색
    @GetMapping("/search")
    public List<FactCheck> searchFactChecks(@RequestParam String keyword) {
        return factCheckRepository.findAll().stream()
                .filter(f -> f.getTitle().contains(keyword) || f.getDescription().contains(keyword))
                .toList();
    }
}
