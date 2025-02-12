from fastapi import FastAPI, HTTPException
import requests
from bs4 import BeautifulSoup
from typing import List
from datetime import datetime, timedelta

app = FastAPI()

NAVER_FACTCHECK_URL = "https://news.naver.com/factcheck/main"

class FactCheck:
    def __init__(self, title: str, description: str, url: str, source: str, timestamp: str):
        self.title = title
        self.description = description
        self.url = url
        self.source = source
        self.timestamp = timestamp

def parse_time_ago(time_str: str) -> datetime:
    """
    네이버 팩트체크 페이지의 타임스탬프를 datetime 객체로 변환하는 함수
    예: "4시간전" → datetime 객체
    """
    now = datetime.now()
    if "시간전" in time_str:
        hours_ago = int(time_str.replace("시간전", "").strip())
        return now - timedelta(hours=hours_ago)
    elif "분전" in time_str:
        minutes_ago = int(time_str.replace("분전", "").strip())
        return now - timedelta(minutes=minutes_ago)
    elif "일전" in time_str:
        days_ago = int(time_str.replace("일전", "").strip())
        return now - timedelta(days=days_ago)
    else:
        return now  # 정확한 시간 파싱이 안 되면 현재 시간 반환

@app.get("/crawl-factchecks", response_model=List[dict])
async def crawl_factchecks():
    try:
        response = requests.get(NAVER_FACTCHECK_URL)
        if response.status_code != 200:
            raise HTTPException(status_code=500, detail="네이버 팩트체크 페이지 요청 실패")

        soup = BeautifulSoup(response.text, "html.parser")
        factcheck_cards = soup.select("li.factcheck_card")

        factchecks = []
        now = datetime.now()
        time_threshold = now - timedelta(hours=12)  # 12시간 이전까지만 허용

        for card in factcheck_cards:
            title = card.select_one(".factcheck_card_title").text.strip()
            description = card.select_one(".factcheck_card_desc").text.strip()
            url = card.select_one(".factcheck_card_link")["href"]
            source = card.select_one(".factcheck_card_sub_item").text.strip()
            timestamp_str = card.select(".factcheck_card_sub_item")[-1].text.strip()
            
            # ✅ 타임스탬프 변환 및 필터링 (12시간 이내)
            article_time = parse_time_ago(timestamp_str)
            if article_time >= time_threshold:
                factchecks.append({
                    "title": title,
                    "description": description,
                    "url": url,
                    "source": source,
                    "timestamp": timestamp_str
                })

        return factchecks
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
