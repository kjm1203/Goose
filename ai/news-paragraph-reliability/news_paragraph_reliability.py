from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from transformers import BartForConditionalGeneration, PreTrainedTokenizerFast
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sentence_transformers import SentenceTransformer
import torch
import numpy as np
import concurrent.futures

app = FastAPI()

device = "cuda" if torch.cuda.is_available() else "cpu"

# ✅ KoBART 요약 모델 로드 (중복 방지 개선)
kobart_model = BartForConditionalGeneration.from_pretrained("digit82/kobart-summarization").to(device)
kobart_tokenizer = PreTrainedTokenizerFast.from_pretrained("digit82/kobart-summarization")

# ✅ BERT 유사도 비교 모델
bert_model = SentenceTransformer("all-MiniLM-L6-v2")

class NewsArticle(BaseModel):
    title: str
    paragraphs: List[str]

class ReferenceNewsArticle(BaseModel):
    title: str
    paragraphs: List[str]

class NewsReliabilityRequest(BaseModel):
    news: NewsArticle
    references: List[ReferenceNewsArticle]

def summarize_text(text):
    """
    KoBART를 이용한 문장 요약 (중복 제거 및 문장 정리 포함)
    """
    if len(text) < 50:  # 너무 짧은 문장은 그대로 반환
        return text

    input_ids = kobart_tokenizer.encode(text, return_tensors="pt", truncation=True, max_length=1024).to(device)
    
    summary_ids = kobart_model.generate(
        input_ids, max_length=60, min_length=20, do_sample=False, num_beams=5
    )
    
    summary = kobart_tokenizer.decode(summary_ids[0], skip_special_tokens=True)

    return clean_summary(summary)

def clean_summary(summary):
    """
    - 중복된 문장 제거
    - 문장이 중간에 끊기지 않도록 정리
    """
    sentences = summary.split('. ')
    sentences = list(set(sentences))  # 중복 제거
    cleaned_summary = '. '.join(sentences)

    if not cleaned_summary.endswith('.'):  # 문장이 중간에 끊기지 않도록 마지막 마침표 추가
        cleaned_summary += '.'
    
    return cleaned_summary

def hybrid_similarity(paragraphs, reference_paragraphs):
    """
    TF-IDF + BERT Hybrid Similarity
    """
    bert_embeddings = bert_model.encode(paragraphs)
    ref_embeddings = bert_model.encode(reference_paragraphs)
    bert_similarity = cosine_similarity(bert_embeddings, ref_embeddings)

    vectorizer = TfidfVectorizer()
    all_texts = paragraphs + reference_paragraphs
    tfidf_matrix = vectorizer.fit_transform(all_texts)
    tfidf_similarity = cosine_similarity(
        tfidf_matrix[:len(paragraphs)], tfidf_matrix[len(paragraphs):]
    )

    return 0.7 * bert_similarity + 0.3 * tfidf_similarity

@app.post("/news/reliability")
async def analyze_news_reliability(request: NewsReliabilityRequest):
    # ✅ 뉴스 문단과 레퍼런스 문단을 원본 그대로 비교
    news_paragraphs = request.news.paragraphs
    ref_paragraphs = [p for ref in request.references for p in ref.paragraphs]

    similarity_matrix = hybrid_similarity(news_paragraphs, ref_paragraphs)

    reliability_scores = [float(np.max(row)) for row in similarity_matrix]
    
    # ✅ best_matches 추출
    best_matches = [ref_paragraphs[np.argmax(row)] for row in similarity_matrix]

    # ✅ best_matches 요약 수행 (병렬 처리)
    with concurrent.futures.ThreadPoolExecutor() as executor:
        summarized_best_matches = list(executor.map(summarize_text, best_matches))

    return {
        "title": request.news.title,
        "paragraph_reliability_scores": reliability_scores,
        "best_evidence_paragraphs": summarized_best_matches
    }
