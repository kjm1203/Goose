from transformers import BertTokenizer, BertModel
import torch
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

# ✅ Sentence-BERT 모델 로드
sbert_model = SentenceTransformer("sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")

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

def merge_short_paragraphs(paragraphs, min_length=10):
    """
    너무 짧은 문단을 이전 문단과 합치는 함수
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

def merge_based_on_similarity(paragraphs, threshold):
    """
    문맥이 유사한 문단을 하나로 합치는 함수 (Sentence-BERT 기반)
    """
    if len(paragraphs) < 2:
        return paragraphs  # 병합할 문단이 하나뿐이면 그대로 반환

    vectors = sbert_model.encode(paragraphs)  # ✅ SBERT 문장 임베딩 생성

    merged_paragraphs = [paragraphs[0]]

    # ✅ 연결어 리스트 (문단 시작 시 병합 기준)
    connection_words = {"특히", "그러나", "게다가", "또한", "이와 함께", "한편", "더불어", "이에 따라", "결과적으로", "즉", "이는"}

    for i in range(1, len(paragraphs)):
        similarity = cosine_similarity([vectors[i - 1]], [vectors[i]])[0][0]

        # ✅ 유사도가 높거나, 문장이 연결어로 시작하면 병합
        if similarity > threshold or paragraphs[i].split()[0] in connection_words:
            merged_paragraphs[-1] += " " + paragraphs[i]
        else:
            merged_paragraphs.append(paragraphs[i])

    return merged_paragraphs

if __name__ == "__main__":
    sample_news = """
    카카오톡, 카나나 등 서비스에 오픈AI 기술 적용 예정.
샘 올트먼 오픈AI CEO도 참석...전략적 제휴 체결 발표.
정신아 카카오 대표가 4일 오전 서울 중구 더 플라자 호텔 서울에서 열린 기자간담회에 샘 올트먼 오픈AI CEO와 함께 참석하고 있다. [사진 연합뉴스].
카카오가 4일 서울 중구 더플라자에서 오픈AI(OpenAI)와 전략적 제휴 체결에 대한 공동 기자간담회를 열었다. 정신아 카카오 대표는 카카오가 꿈꾸는 AI 미래시대에 대해 이야기하며, 카카오가 지향하는 AI 전략 방향을 설명했다. 이 날 간담회에는 정신아 카카오 대표를 비롯해 샘 올트먼 오픈AI 최고경영자(CEO)가 직접 참석했다.
카카오의 AI 사업 방향성은 직접 개발보다 이미 개발된 AI 모델 중 최고의 모델을 가져와 적용하는 것. 카카오가 찾은 협력사는 챗GPT 개발사인 오픈AI로, 국내에서는 처음으로 오픈AI와의 전략적 제휴를 맺었다.
양사는 지난해 9월부터 관련 논의를 진행해왔고, 'AI 보편화' 'AI 대중화'라는 쟁점에서 두 기업의 지향점이 일치해 실질적인 협력까지 이어졌다. 특히 카카오는 5000만명 대한민국 국민이 사용하고 있는 '국민 채팅앱'이라는 부분에서 오픈AI의 사용자 확장 목표를 채울 것으로 평가된다.
발언하는 정신아 카카오 대표. [사진 연합뉴스].
정신아 카카오 대표는 이번 발표에서 “오랜 기간 국민 다수의 일상을 함께 하며 축적해 온 역량을 바탕으로 ‘이용자를 가장 잘 이해하는 개인화된 AI’를 선보이는 것이 지금 시대 카카오의 역할일 것”이라며 “글로벌 기술 경쟁력을 보유한 오픈AI와 협력해 혁신적 고객경험을 제공함으로써 AI 서비스의 대중화를 이끌겠다”고 말했다.
    """


    # 1️⃣ 문단 나누기 (BERT 기반)
    paragraphs = split_paragraphs_bert(sample_news)
    print("=======================1️⃣ 문단 나누기 (BERT 기반)========================")
    for i, para in enumerate(paragraphs):
        print(f"문단 {i+1}: {para}")
        print("문단 길이 : ", len(para))

    # 2️⃣ 너무 짧은 문단을 앞 문단과 합치기
    paragraphs = merge_short_paragraphs(paragraphs, min_length=10)
    print("=======================2️⃣ 너무 짧은 문단을 앞 문단과 합치기========================")
    for i, para in enumerate(paragraphs):
        print(f"문단 {i+1}: {para}\n")

    # 3️⃣ 문맥이 유사한 문단 합치기 (코사인 유사도 기반)
    merged_paragraphs = merge_based_on_similarity(paragraphs, threshold=0.9)

    # 4️⃣ 최종 출력
    print("=======================4️⃣ 최종 출력========================")
    for i, para in enumerate(merged_paragraphs):
        print(f"문단 {i+1}: {para}\n")
