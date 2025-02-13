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

# ✅ KoBART 요약 모델 로드
kobart_model = BartForConditionalGeneration.from_pretrained("digit82/kobart-summarization").to(device)
kobart_tokenizer = PreTrainedTokenizerFast.from_pretrained("digit82/kobart-summarization")

# ✅ BERT 유사도 비교 모델
bert_model = SentenceTransformer("all-MiniLM-L6-v2")

class NewsArticle(BaseModel):
    paragraphs: List[str]
    keywords: List[str]

class ReferenceNewsArticle(BaseModel):
    paragraphs: List[str]
    keywords: List[str]

class NewsReliabilityRequest(BaseModel):
    news: NewsArticle
    references: List[ReferenceNewsArticle]

def summarize_text(text: str) -> str:
    if len(text) < 50:
        return text

    input_ids = kobart_tokenizer.encode(text, return_tensors="pt", truncation=True, max_length=1024).to(device)
    summary_ids = kobart_model.generate(
        input_ids, max_length=60, min_length=20, do_sample=False, num_beams=5
    )
    summary = kobart_tokenizer.decode(summary_ids[0], skip_special_tokens=True)
    return clean_summary(summary)

def clean_summary(summary: str) -> str:
    sentences = summary.split('. ')
    sentences = list(set(sentences))
    cleaned_summary = '. '.join(sentences)
    if not cleaned_summary.endswith('.'):
        cleaned_summary += '.'
    return cleaned_summary

def hybrid_similarity(paragraphs, reference_paragraphs):
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

def summarize_text_with_keywords(text: str, keywords: List[str]) -> str:
    """
    주요 키워드를 반영하여 텍스트를 한 문장으로 요약합니다.
    1. KoBART 모델에 프롬프트를 추가해 요약을 시도합니다.
    2. 만약 오류 발생 시, 텍스트를 문장 단위로 나눈 뒤 키워드가 가장 많이 포함된 문장을 반환합니다.
    """
    prompt = f"다음 텍스트를 주요 키워드 {', '.join(keywords)}를 반영하여 한 문장으로 간결하게 요약해줘: {text}"
    try:
        input_ids = kobart_tokenizer.encode(prompt, return_tensors="pt", truncation=True, max_length=1024).to(device)
        summary_ids = kobart_model.generate(
            input_ids, max_length=60, min_length=20, do_sample=False, num_beams=5
        )
        summary = kobart_tokenizer.decode(summary_ids[0], skip_special_tokens=True)
        summary = clean_summary(summary)
        # 한 문장으로 만들기 위해 첫 문장만 선택
        sentences = summary.split('. ')
        best_sentence = sentences[0].strip()
        if best_sentence and not best_sentence.endswith('.'):
            best_sentence += '.'
        return best_sentence
    except Exception as e:
        print(f"KoBART 프롬프트 요약 실패: {e}")
        # fallback: 키워드 기반으로 가장 많이 포함된 문장 선택
        sentences = text.split('. ')
        best_sentence = ""
        max_count = 0
        for sentence in sentences:
            count = sum(1 for keyword in keywords if keyword in sentence)
            if count > max_count:
                max_count = count
                best_sentence = sentence.strip()
        if best_sentence and not best_sentence.endswith('.'):
            best_sentence += '.'
        return best_sentence if best_sentence else text

@app.post("/news/reliability")
async def analyze_news_reliability(request: NewsReliabilityRequest):
    keywords = request.news.keywords

    # ✅ 1. 모든 뉴스 문단 선택 (키워드 포함 여부 검사 제외)
    news_paragraphs = request.news.paragraphs

    if not news_paragraphs:
        return {
            "paragraph_reliability_scores": [],
            "best_evidence_paragraphs": []
        }

    # ✅ 2. 레퍼런스 뉴스 문단은 그대로 사용
    ref_paragraphs = [p for ref in request.references for p in ref.paragraphs]

    if not ref_paragraphs:
        return {
            "paragraph_reliability_scores": [],
            "best_evidence_paragraphs": []
        }

    # ✅ 3. 유사도 측정
    similarity_matrix = hybrid_similarity(news_paragraphs, ref_paragraphs)

    reliability_scores = [float(np.max(row)) for row in similarity_matrix]
    best_matches = [ref_paragraphs[np.argmax(row)] for row in similarity_matrix]

    # ✅ 4. 병렬 요약 - 수정된 함수 사용 (주요 키워드를 반영하여 한 문장으로 요약)
    with concurrent.futures.ThreadPoolExecutor() as executor:
        summarized_best_matches = list(
            executor.map(lambda text: summarize_text_with_keywords(text, keywords), best_matches)
        )

    return {
        "paragraph_reliability_scores": reliability_scores,
        "best_evidence_paragraphs": summarized_best_matches
    }
