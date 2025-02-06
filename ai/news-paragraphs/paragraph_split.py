from transformers import BertTokenizer, BertModel
import torch
import numpy as np
import kss
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def get_sentence_embeddings(text):
    """
    KoBERT를 활용하여 문장의 임베딩 벡터를 얻음.
    """
    tokenizer = BertTokenizer.from_pretrained("monologg/kobert")
    model = BertModel.from_pretrained("monologg/kobert")

    sentences = kss.split_sentences(text)
    embeddings = []

    for sent in sentences:
        tokens = tokenizer(sent, return_tensors="pt", padding=True, truncation=True)
        with torch.no_grad():
            outputs = model(**tokens)
        embeddings.append(outputs.last_hidden_state.mean(dim=1).numpy())

    return sentences, np.array(embeddings).squeeze()

def split_paragraphs_bert(text, threshold=0.8):
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

def merge_short_paragraphs(paragraphs, min_length=30):
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

def merge_based_on_similarity(paragraphs, threshold=0.7):
    """
    문맥이 유사한 문단을 하나로 합치는 함수
    """
    if len(paragraphs) < 2:
        return paragraphs  # 문단이 하나면 병합할 필요 없음

    vectorizer = TfidfVectorizer()
    vectors = vectorizer.fit_transform(paragraphs).toarray()

    merged_paragraphs = [paragraphs[0]]

    for i in range(1, len(paragraphs)):
        similarity = cosine_similarity([vectors[i - 1]], [vectors[i]])[0][0]

        if similarity > threshold:  # 유사도가 높으면 문단 병합
            merged_paragraphs[-1] += " " + paragraphs[i]
        else:
            merged_paragraphs.append(paragraphs[i])

    return merged_paragraphs

if __name__ == "__main__":
    sample_news = """
    서울 중구 명동 올리브영 플래그십 매장 앞을 시민들이 오가고 있다. 뉴시스
    CJ올리브영이 세계 최대 뷰티 시장인 미국에 진출한다. 현지 법인 설립을 시작으로 향후 오프라인 점포 개점까지 진행한다는 계획이다.
    4일 올리브영은 미국 LA에 현지 법인 ‘CJ Olive Young USA’를 설립하고 본격적인 미국 진출에 나선다고 밝혔다. 현지 판매 전 상품 소싱, 마케팅, 물류시스템 등 사업 확장을 위한 핵심 기능의 현지화를 추진하는 단계다.
    미국 내 오프라인 1호점 매장 개점도 추진한다. 올리브영 측은 “현재 후보 부지를 두고 검토하고 있다”고 밝혔다. 물류망도 구축할 예정이다. 올해는 올리브영 글로벌몰과 한국 본사 시스템을 연동해 재고 입출고를 실시간으로 관리한다. 향후에는 CJ대한통운 미국 법인과 협업해 현지에서 고객에게 직접 상품을 발송할 계획이다.
    올리브영 측은 “미국 법인 설립으로 현지 소비자들은 보다 다양한 할인 행사를 경험할 수 있을 것”이라고 밝혔다. 지금도  미국의 소비자들은 올리브영 글로벌몰을 통해 상품을 구매할 수 있지만 현지 법인이 생기면 소비자 니즈를 분석해 현지 고객이 원하는 상품을 소싱하고 상품 큐레이션을 고도화할 수 있다는 설명이다.
    올리브영은 미국 진출 배경에 대해 “시장 규모나 파급력 측면에서 매력적인 시장”이라며 “미국을 ‘글로벌 K뷰티 1위 플랫폼’ 도약을 위한 전진기지로 삼고 K뷰티 글로벌화를 가속화하겠다”고 밝혔다. 유로모니터에 따르면 2023년 미국 뷰티 시장 규모는 약 1200억 달러로 단일 국가 중 규모가 가장 크고 전 세계(5700억 달러) 뷰티 시장 가운데 21%를 차지한다.
    이선정 올리브영 대표는 “K-뷰티 산업의 성장세가 지속되도록 해외 시장에서 ‘성장 부스터’ 역할을 할 것”이라고 말했다.
    """

    # 1️⃣ 문단 나누기 (BERT 기반)
    paragraphs = split_paragraphs_bert(sample_news)

    # 2️⃣ 너무 짧은 문단을 앞 문단과 합치기
    paragraphs = merge_short_paragraphs(paragraphs, min_length=10)

    # 3️⃣ 문맥이 유사한 문단 합치기 (코사인 유사도 기반)
    merged_paragraphs = merge_based_on_similarity(paragraphs, threshold=0.7)

    # 4️⃣ 최종 출력
    for i, para in enumerate(merged_paragraphs):
        print(f"문단 {i+1}: {para}\n")
