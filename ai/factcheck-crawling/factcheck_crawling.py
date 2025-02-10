from fastapi import FastAPI, HTTPException
import requests
from bs4 import BeautifulSoup
from typing import List

app = FastAPI()

NAVER_FACTCHECK_URL = "https://news.naver.com/factcheck/main"

class FactCheck:
    def __init__(self, title: str, description: str, url: str, source: str, timestamp: str):
        self.title = title
        self.description = description
        self.url = url
        self.source = source
        self.timestamp = timestamp

@app.get("/crawl-factchecks", response_model=List[dict])
async def crawl_factchecks():
    try:
        response = requests.get(NAVER_FACTCHECK_URL)
        if response.status_code != 200:
            raise HTTPException(status_code=500, detail="네이버 팩트체크 페이지 요청 실패")

        soup = BeautifulSoup(response.text, "html.parser")
        factcheck_cards = soup.select("li.factcheck_card")

        factchecks = []
        for card in factcheck_cards:
            title = card.select_one(".factcheck_card_title").text.strip()
            description = card.select_one(".factcheck_card_desc").text.strip()
            url = card.select_one(".factcheck_card_link")["href"]
            source = card.select_one(".factcheck_card_sub_item").text.strip()
            timestamp = card.select(".factcheck_card_sub_item")[-1].text.strip()

            factchecks.append({
                "title": title,
                "description": description,
                "url": url,
                "source": source,
                "timestamp": timestamp
            })

        return factchecks
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
