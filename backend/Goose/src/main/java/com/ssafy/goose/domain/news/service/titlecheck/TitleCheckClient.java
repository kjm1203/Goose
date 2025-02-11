package com.ssafy.goose.domain.news.service.titlecheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TitleCheckClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FACT_CHECK_API_URL = "http://localhost:5057/title-compare-contents";

    public double checkTitleWithReference(String title, List<String> referenceContents) {
        try {
            int totalArticles = referenceContents.size();
            if (totalArticles == 0) return 0.0; // 데이터가 없으면 0 반환

            double totalScore = 0.0;

            for (String content : referenceContents) {
                // ✅ 요청 데이터 생성
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("title", title);
                requestBody.put("content", content);

                // ✅ HTTP 요청 설정
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(requestBody), headers);

                // ✅ FastAPI 서버 호출
                ResponseEntity<String> response = restTemplate.postForEntity(FACT_CHECK_API_URL, requestEntity, String.class);

                // ✅ JSON 응답 파싱
                Map<String, Double> responseBody = new ObjectMapper().readValue(response.getBody(), new TypeReference<Map<String, Double>>() {});
                double similarity_score = responseBody.get("similarity_score");

                System.out.println("similarity_score : " + similarity_score);

                // ✅ 점수 합산
                totalScore += similarity_score;
            }

            // ✅ 평균 점수 계산 (0.0 ~ 1.0 범위)
            double averageScore = totalScore * 100 / totalArticles;
            System.out.println("averageScore (0.0 ~ 100.0 범위) : " + averageScore);

            return averageScore; // 최종 평균 유사도 반환

        } catch (Exception e) {
            e.printStackTrace();
            return 50.0; // 오류 발생 시 50 반환
        }
    }
}
