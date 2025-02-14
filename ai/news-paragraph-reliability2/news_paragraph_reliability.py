#!/usr/bin/env python
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import openai
import chromadb
from sentence_transformers import SentenceTransformer
import numpy as np
import concurrent.futures
import torch

# OpenAI API Key 설정 (실제 키로 교체)
openai.api_key = "YOUR_OPENAI_API_KEY"

app = FastAPI()

device = "cuda" if torch.cuda.is_available() else "cpu"

# SentenceTransformer 모델 로드 (임베딩 계산용)
bert_model = SentenceTransformer("all-MiniLM-L6-v2")

# ChromaDB 연결 정보 (뉴스 reference 문단 임베딩이 저장된 컬렉션)
CHROMA_HOST = "i12d208.p.ssafy.io"   # 도메인만 사용
CHROMA_PORT = 8000                   # 외부 포트 번호
CHROMA_COLLECTION_NAME = "reference_paragraphs"

# ChromaDB 클라이언트 생성 및 컬렉션 가져오기
chroma_client = chromadb.HttpClient(host=CHROMA_HOST, port=CHROMA_PORT)
chroma_collection = chroma_client.get_or_create_collection(name=CHROMA_COLLECTION_NAME)

# 컬렉션 내 문서 개수 확인
doc_count = chroma_collection.count()
print(f"저장된 문서 개수: {doc_count}")

# Pydantic 모델 정의
class NewsArticle(BaseModel):
    paragraphs: List[str]
    keywords: List[str]

class NewsReliabilityRequest(BaseModel):
    news: NewsArticle

def query_similar_paragraph(query_text: str, n_results: int = 1):
    """
    주어진 문단(query_text)에 대해 ChromaDB에서 가장 유사한 문단과 거리를 반환.
    """
    query_embedding = bert_model.encode([query_text])[0]
    result = chroma_collection.query(
        query_embeddings=[query_embedding.tolist()],
        n_results=n_results,
        include=["documents", "distances"]
    )
    if result and "documents" in result and result["documents"] and result["documents"][0]:
        best_doc = result["documents"][0][0]
        best_distance = result["distances"][0][0]
        # 유사도 계산: similarity = 1/(1+distance)
        similarity_score = 1 / (1 + best_distance)
        return best_doc, similarity_score
    return None, 0.0

def summarize_with_chatgpt(text: str, keywords: List[str]) -> str:
    """
    ChatGPT API를 호출하여, 입력 텍스트를 주요 키워드를 반영한 한 문장으로 간결하게 요약.
    """
    prompt = f"다음 텍스트를 주요 키워드 {', '.join(keywords)}를 반영하여 한 문장으로 간결하게 요약해줘: {text}"
    try:
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "너는 뉴스 요약 전문가이다."},
                {"role": "user", "content": prompt}
            ],
            max_tokens=60,
            n=1,
            temperature=0.5
        )
        summary = response.choices[0].message["content"].strip()
        return summary
    except Exception as e:
        print(f"ChatGPT 요약 실패: {e}")
        return text

@app.post("/news/reliability")
async def analyze_news_reliability(request: NewsReliabilityRequest):
    keywords = request.news.keywords
    news_paragraphs = request.news.paragraphs

    if not news_paragraphs:
        return {
            "paragraph_reliability_scores": [],
            "best_evidence_paragraphs": []
        }
    
    best_evidence_paragraphs = []
    reliability_scores = []

    # 각 뉴스 문단마다, ChromaDB에서 가장 유사한 문단 검색 후 ChatGPT로 요약
    for paragraph in news_paragraphs:
        similar_doc, similarity = query_similar_paragraph(paragraph)
        if similar_doc:
            summarized = summarize_with_chatgpt(similar_doc, keywords)
        else:
            summarized = ""
        best_evidence_paragraphs.append(summarized)
        reliability_scores.append(similarity)

    return {
        "paragraph_reliability_scores": reliability_scores,
        "best_evidence_paragraphs": best_evidence_paragraphs
    }
