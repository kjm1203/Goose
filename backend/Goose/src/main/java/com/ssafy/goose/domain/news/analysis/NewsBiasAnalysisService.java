package com.ssafy.goose.domain.news.analysis;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsBiasAnalysisService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String fastApiUrl = "http://i12d208.p.ssafy.io:5051/analyze-bias"; // FastAPI 주소

    public Double getBiasScore(List<String> contents, String targetContent, String keyword) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("keyword", keyword);
        requestBody.put("contents", contents);
        requestBody.put("target_content", targetContent);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("biasScore")) {
            return ((Number) response.getBody().get("biasScore")).doubleValue();
        }
        return 50.0; // 기본값 (편향성 중립)
    }
}
