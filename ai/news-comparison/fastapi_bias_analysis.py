from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI()

class BiasRequest(BaseModel):
    keyword: str
    contents: List[str]  # 같은 키워드로 검색된 뉴스들의 본문 리스트
    target_content: str  # 현재 뉴스의 본문

@app.post("/analyze-bias")
async def analyze_bias(request: BiasRequest):
    try:
        all_contents = request.contents + [request.target_content]  # 기존 뉴스 + 현재 뉴스
        vectorizer = TfidfVectorizer(stop_words='english')  # TF-IDF 벡터 변환
        tfidf_matrix = vectorizer.fit_transform(all_contents)  # TF-IDF 변환

        similarities = cosine_similarity(tfidf_matrix)  # 뉴스 간 유사도 계산
        target_similarities = similarities[-1][:-1]  # 현재 뉴스와 기존 뉴스들의 유사도

        threshold = 0.6  # ✅ 60% 이상의 유사도를 가지면 같은 논조로 판단
        similar_count = np.sum(target_similarities > threshold)  # 같은 논조 기사 개수
        total_count = len(target_similarities)  # 전체 기사 개수

        # ✅ ZeroDivisionError 방지
        if total_count == 0:
            bias_score = 50.0  # 기존 뉴스 없음 → 50
        else:
            bias_score = (1 - (similar_count / total_count)) * 100 # 편향성 계산 (낮을수록 중립적)
        
        return {"biasScore": round(bias_score, 2)}  # 반올림 후 반환

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
