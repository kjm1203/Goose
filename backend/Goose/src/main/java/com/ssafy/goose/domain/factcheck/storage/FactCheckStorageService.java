package com.ssafy.goose.domain.factcheck.storage;

import com.ssafy.goose.domain.factcheck.model.FactCheck;
import com.ssafy.goose.domain.factcheck.repository.FactCheckRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactCheckStorageService {
    private final FactCheckRepository factCheckRepository;

    public FactCheckStorageService(FactCheckRepository factCheckRepository) {
        this.factCheckRepository = factCheckRepository;
    }

    public void saveFactChecks(List<FactCheck> factChecks) {
        factCheckRepository.saveAll(factChecks);
        System.out.println("✅ 팩트체크 데이터 저장 완료! (" + factChecks.size() + "개)");
    }
}
