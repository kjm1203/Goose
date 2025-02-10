package com.ssafy.goose.domain.factcheck.crawling;

import com.ssafy.goose.domain.factcheck.model.FactCheck;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FactCheckCrawlerService {
    private static final String NAVER_FACTCHECK_URL = "https://factcheck.naver.com/";

    public List<FactCheck> fetchFactChecks() {
        List<FactCheck> factChecks = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(NAVER_FACTCHECK_URL).get();
            Elements factCheckCards = doc.select("li.factcheck_card");  // ✅ 팩트체크 카드 목록

            for (Element card : factCheckCards) {
                String title = card.select(".factcheck_card_title").text();
                String description = card.select(".factcheck_card_desc").text();
                String url = card.select(".factcheck_card_link").attr("href");
                String source = card.select(".factcheck_card_sub_item").first().text();
                String timestamp = card.select(".factcheck_card_sub_item").last().text();

                FactCheck factCheck = new FactCheck(null, title, description, url, source, timestamp);
                factChecks.add(factCheck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factChecks;
    }
}
