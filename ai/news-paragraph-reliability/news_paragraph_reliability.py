from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sentence_transformers import SentenceTransformer
import numpy as np

app = FastAPI()

# ✅ 더 정밀한 BERT 모델 사용
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

def normalize_paragraphs(paragraphs):
    """
    너무 짧거나 너무 긴 문단을 제거하고, 일정 길이로 정규화
    """
    cleaned_paragraphs = []
    for para in paragraphs:
        if len(para) < 50:
            continue
        sentences = para.split('. ')
        cleaned_paragraphs.append('. '.join(sentences[:3]))

    return cleaned_paragraphs

@app.post("/news/reliability")
async def analyze_news_reliability(request: NewsReliabilityRequest):
    news_paragraphs = normalize_paragraphs(request.news.paragraphs)
    ref_paragraphs = normalize_paragraphs([p for ref in request.references for p in ref.paragraphs])

    similarity_matrix = hybrid_similarity(news_paragraphs, ref_paragraphs)

    reliability_scores = [float(np.max(row)) for row in similarity_matrix]
    best_matches = [ref_paragraphs[np.argmax(row)] for row in similarity_matrix]

    return {
        "title": request.news.title,
        "paragraph_reliability_scores": reliability_scores,
        "best_evidence_paragraphs": best_matches
    }
