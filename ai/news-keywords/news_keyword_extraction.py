from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import spacy
from collections import Counter

# ✅ FastAPI 서버 초기화
app = FastAPI()

# ✅ 자연어처리(NLP) 모델 로드 (spaCy 사용)
# nlp = spacy.load("en_core_web_sm")  # 영어 뉴스의 경우
nlp = spacy.load("ko_core_news_sm")  # 한국어 뉴스의 경우 (설치 필요)

class KeywordRequest(BaseModel):
    titles: List[str]

@app.post("/extract-keywords")
async def extract_keywords(request: KeywordRequest):
    try:
        all_text = " ".join(request.titles)  # 제목들을 하나의 문자열로 합침
        doc = nlp(all_text)
        print("all_text : ", all_text)

        # ✅ 명사(NOUN)와 고유명사(PROPN) 추출
        keywords = [token.text for token in doc if token.pos_ in ["NOUN", "PROPN"]]

        # ✅ 빈도수 기반 상위 10개 키워드 선택
        keyword_counts = Counter(keywords)
        top_keywords = [word for word, count in keyword_counts.most_common(10)]
        print("top_keywords : ", top_keywords)

        return {"keywords": top_keywords}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
