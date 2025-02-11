from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import torch
from transformers import (
    BartForConditionalGeneration, PreTrainedTokenizerFast,
    AutoModelForSequenceClassification, AutoTokenizer
)

app = FastAPI()

# ✅ 1. KoBART 모델 로드 (텍스트 요약)
kobart_model = BartForConditionalGeneration.from_pretrained("digit82/kobart-summarization")
kobart_tokenizer = PreTrainedTokenizerFast.from_pretrained("digit82/kobart-summarization")

# ✅ 2. KLUE-RoBERTa 모델 로드 (자연어 추론, "klue/roberta-base" 사용)
nli_model = AutoModelForSequenceClassification.from_pretrained("klue/roberta-base")
nli_tokenizer = AutoTokenizer.from_pretrained("klue/roberta-base")

class FactCheckRequest(BaseModel):
    title: str
    content: str

@app.post("/factcheck")
async def fact_check(request: FactCheckRequest):
    try:
        title = request.title
        content = request.content

        # ✅ 1. 뉴스 본문 요약
        inputs = kobart_tokenizer.encode("summarize: " + content, return_tensors="pt", max_length=512, truncation=True)
        summary_ids = kobart_model.generate(inputs, max_length=150, num_beams=4, early_stopping=True)
        summary = kobart_tokenizer.decode(summary_ids[0], skip_special_tokens=True)

        print("🔹 [메인 뉴스 타이틀] :", title)
        print("🔹 [요약된 내용] :", summary)

        # ✅ 2. 자연어 추론 (팩트 검증)
        premise = summary  # 요약된 본문 (전제)
        hypothesis = title  # 팩트체크 제목 (가설)

        # 🔹 부정형 제목인 경우 반대 문장을 추가
        if any(neg_word in title for neg_word in ["없다", "아니다", "아닌"]):
            reversed_hypothesis = title.replace("없다", "있다").replace("아니다", "이다").replace("아닌", "인")
            hypothesis += " / " + reversed_hypothesis  # 원래 문장 + 반대 문장 함께 사용

        encoded_input = nli_tokenizer(premise, hypothesis, return_tensors="pt", truncation=True, padding=True)
        output = nli_model(**encoded_input)
        logits = output.logits
        probs = torch.nn.functional.softmax(logits, dim=1)

        # ✅ 3. 출력 크기 확인 후 안전한 접근
        num_classes = probs.shape[1]  # 모델의 실제 출력 크기
        entailment_prob = probs[0][0].item()  # True 확률

        if num_classes == 3:
            contradiction_prob = probs[0][2].item()  # False 확률 (3-class 지원하는 경우)
        else:
            contradiction_prob = 1 - entailment_prob  # 2-class 모델인 경우 보완

        # ✅ 4. 확률값 기반 판별 로직 개선
        if contradiction_prob > 0.5:
            result = "False"
        elif entailment_prob > 0.5:
            result = "True"
        else:
            result = "Partially True" 

        print(f"🔹 [검증 결과] : {result} (Entailment: {entailment_prob:.2f}, Contradiction: {contradiction_prob:.2f})")

        return {"title": title, "summary": summary, "factcheck_result": result}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
