package com.ssafy.goose.domain.news.service.titlecheck;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TitleCheckClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_URL = "http://127.0.0.1:5056/titlecheck";

    public String checkTitleWithReference(String title, List<String> referenceContents) {
        Map<String, Object> request = new HashMap<>();
        request.put("title", title);
        request.put("reference_contents", referenceContents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(FASTAPI_URL, HttpMethod.POST, entity, Map.class);
        return response.getBody().get("factcheck_result").toString();
    }
}
