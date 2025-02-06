from transformers import BertTokenizer, BertModel
import torch
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from sentence_transformers import SentenceTransformer
from soynlp.tokenizer import RegexTokenizer  # ✅ KoNLPy 대신 soynlp 사용
import os

# ✅ Sentence-BERT 모델 로드
sbert_model = SentenceTransformer("sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")

# ✅ 형태소 분석기 변경 (soynlp)
tokenizer = RegexTokenizer()

def is_connection_word(sentence):
    """
    문장이 접속 부사(혹은 연결어)로 시작하는지 확인 (soynlp 기반)
    """
    connection_words = {"특히", "그러나", "게다가", "또한", "이와 함께", "한편", "더불어", "이에 따라", "결과적으로", "즉", "이는"}
    words = tokenizer.tokenize(sentence)
    
    if words and words[0] in connection_words:
        return True
    return False

def split_sentences_by_period(text):
    """
    문장을 온점('.')을 기준으로 나눠 리스트로 반환
    """
    sentences = [s.strip() for s in text.split('.') if s.strip()]
    return [s + '.' for s in sentences]  # 각 문장 끝에 '.'을 유지

def get_sentence_embeddings(text):
    """
    KoBERT를 활용하여 문장의 임베딩 벡터를 얻음.
    """
    tokenizer = BertTokenizer.from_pretrained("monologg/kobert")
    model = BertModel.from_pretrained("monologg/kobert")

    sentences = split_sentences_by_period(text)  # 🔹 온점 기준으로 문장 분리
    embeddings = []

    for sent in sentences:
        tokens = tokenizer(sent, return_tensors="pt", padding=True, truncation=True)
        with torch.no_grad():
            outputs = model(**tokens)
        embeddings.append(outputs.last_hidden_state.mean(dim=1).numpy())

    return sentences, np.array(embeddings).squeeze()

def split_paragraphs_bert(text, threshold=0.9):
    """
    문장 간 의미적 유사도를 측정하여 문단을 구분.
    """
    sentences, embeddings = get_sentence_embeddings(text)
    similarities = cosine_similarity(embeddings)

    paragraphs = []
    current_paragraph = [sentences[0]]

    for i in range(1, len(sentences)):
        if similarities[i - 1][i] < threshold:  # 유사도가 낮으면 새로운 문단 시작
            paragraphs.append(" ".join(current_paragraph))
            current_paragraph = [sentences[i]]
        else:
            current_paragraph.append(sentences[i])

    if current_paragraph:
        paragraphs.append(" ".join(current_paragraph))

    return paragraphs

def merge_short_paragraphs(paragraphs, min_length=15):
    """
    너무 짧은 문단을 앞 문단과 합치는 함수
    """
    fixed_paragraphs = []
    current_paragraph = ""

    for paragraph in paragraphs:
        if len(paragraph) < min_length and fixed_paragraphs:
            # 현재 문장이 너무 짧으면 이전 문장과 합침
            fixed_paragraphs[-1] += " " + paragraph
        else:
            fixed_paragraphs.append(paragraph)

    return fixed_paragraphs

def merge_based_on_similarity(paragraphs, threshold=0.85):
    """
    문맥이 유사한 문단을 하나로 합치는 함수 (Sentence-BERT 기반)
    """
    if len(paragraphs) < 2:
        return paragraphs  # 병합할 문단이 하나뿐이면 그대로 반환

    vectors = sbert_model.encode(paragraphs)  # ✅ SBERT 문장 임베딩 생성

    merged_paragraphs = [paragraphs[0]]

    for i in range(1, len(paragraphs)):
        similarity = cosine_similarity([vectors[i - 1]], [vectors[i]])[0][0]

        # ✅ 유사도가 높거나, 문장이 접속 부사로 시작하면 병합
        if similarity > threshold or is_connection_word(paragraphs[i]):
            merged_paragraphs[-1] += " " + paragraphs[i]
        else:
            merged_paragraphs.append(paragraphs[i])

    return merged_paragraphs

def merge_based_on_naturalness(paragraphs, threshold=0.85):
    """
    문맥적 자연스러움을 고려하여 문장을 병합하는 함수
    """
    if len(paragraphs) < 2:
        return paragraphs

    merged_paragraphs = [paragraphs[0]]

    for i in range(1, len(paragraphs)):
        # ✅ 앞 문장과 현재 문장을 연결했을 때의 자연스러움 평가
        combined_text = merged_paragraphs[-1] + " " + paragraphs[i]
        
        # ✅ 두 개의 문장 묶음의 SBERT 벡터 생성
        original_vector = sbert_model.encode([paragraphs[i]])
        combined_vector = sbert_model.encode([combined_text])
        
        # ✅ 유사도 계산
        similarity = cosine_similarity(original_vector, combined_vector)[0][0]

        if similarity > threshold:  # 연결이 자연스럽다면 병합
            merged_paragraphs[-1] += " " + paragraphs[i]
        else:
            merged_paragraphs.append(paragraphs[i])

    return merged_paragraphs

def final_merge_paragraphs(paragraphs, threshold=0.85, min_length=15):
    """
    문맥적 유사도, 문장 자연스러움, 문장 길이를 고려한 최적의 병합 방식
    """
    # 1️⃣ 문장이 너무 짧으면 앞 문장과 병합
    paragraphs = merge_short_paragraphs(paragraphs, min_length=min_length)

    # 2️⃣ 문장이 연결어로 시작하면 앞 문장과 병합
    paragraphs = merge_based_on_similarity(paragraphs, threshold=threshold)

    # 3️⃣ 문장이 문맥적으로 자연스럽다면 앞 문장과 병합
    paragraphs = merge_based_on_naturalness(paragraphs, threshold=threshold)

    return paragraphs

def load_text_file(file_path):
    """
    주어진 파일에서 텍스트를 읽어 반환하는 함수
    """
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            return file.read().strip()  # 앞뒤 공백 제거
    except FileNotFoundError:
        print(f"❌ 파일을 찾을 수 없습니다: {file_path}")
        return ""

if __name__ == "__main__":
    # 🔹 news.txt 파일에서 뉴스 텍스트 불러오기
    sample_news = load_text_file("news.txt")

    if not sample_news:
        print("❌ 뉴스 데이터가 없습니다. 파일을 확인하세요.")
    else:
        paragraphs = split_paragraphs_bert(sample_news)
        paragraphs = final_merge_paragraphs(paragraphs, threshold=0.85, min_length=15)

        print("======================= 최종 출력 ========================")
        for i, para in enumerate(paragraphs):
            print(f"문단 {i+1}: {para}\n")
