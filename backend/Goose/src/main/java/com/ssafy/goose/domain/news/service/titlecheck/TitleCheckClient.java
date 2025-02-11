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
    private final RestTemplate restTemplate= new RestTemplate();
    private static final String FACT_CHECK_API_URL = "http://localhost:5057/factcheck";


    public String checkTitleWithReference(String title, List<String> referenceContents) {
        try {
            int totalArticles = referenceContents.size();
            if (totalArticles == 0) return "No Relevant Data";

            // ✅ 각 뉴스별 분석 결과 저장 (True: 2점, Partially True: 1점, False: 0점)
            int totalScore = 0;

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
                Map<String, String> responseBody = new ObjectMapper().readValue(response.getBody(), new TypeReference<Map<String, String>>() {});
                String factCheckResult = responseBody.get("factcheck_result");

                System.out.println("factCheckResult : " + factCheckResult);
                // ✅ 결과 점수 매기기 (True: 2, Partially True: 1, False: 0)
                if ("True".equalsIgnoreCase(factCheckResult)) {
                    totalScore += 2;
                } else if ("Partially True".equalsIgnoreCase(factCheckResult)) {
                    totalScore += 1;
                }
            }

            // ✅ 평균 점수 계산 (0.0 ~ 2.0 범위)
            double averageScore = (double) totalScore / totalArticles;
            System.out.println("averageScore (0.0 ~ 2.0 범위) : " + averageScore);

            // ✅ 최종 결과 판별
            if (averageScore >= 1.5) {
                return "True";
            } else if (averageScore >= 0.5) {
                return "Partially True";
            } else {
                return "False";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling FastAPI service";
        }
    }
}
