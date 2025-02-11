package com.ssafy.goose.domain.factcheck.crawling;

import com.ssafy.goose.domain.factcheck.model.FactCheck;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class FactCheckCrawlerService {
    private final RestTemplate restTemplate;
    private static final String FACTCHECK_CRAWLING_URL = "http://localhost:5054/crawl-factchecks";

    public FactCheckCrawlerService() {
        this.restTemplate = new RestTemplate();
    }

    public List<FactCheck> fetchFactChecks() {
        FactCheck[] response = restTemplate.getForObject(FACTCHECK_CRAWLING_URL, FactCheck[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }
}
