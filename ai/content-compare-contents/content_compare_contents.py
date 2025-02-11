from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import torch
from transformers import (
    BartForConditionalGeneration, PreTrainedTokenizerFast,
    AutoModelForSequenceClassification, AutoTokenizer
)

app = FastAPI()

# ✅ GPU 또는 CPU 자동 감지
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# ✅ 1. KoBART 모델 로드 (텍스트 요약)
kobart_model = BartForConditionalGeneration.from_pretrained("digit82/kobart-summarization").to(device)
kobart_tokenizer = PreTrainedTokenizerFast.from_pretrained("digit82/kobart-summarization")

# ✅ 2. KLUE-RoBERTa Large 모델 로드 (자연어 추론)
nli_model = AutoModelForSequenceClassification.from_pretrained("klue/roberta-large").to(device)
nli_tokenizer = AutoTokenizer.from_pretrained("klue/roberta-large")

class FactCheckRequest(BaseModel):
    content: str
    referenceContent: str

@app.post("/title-compare-contents")
async def fact_check(request: FactCheckRequest):
    try:        
        content = request.content
        referenceContent = request.referenceContent

        # ✅ 1. 뉴스 본문 요약 (KoBART)
        inputs_main = kobart_tokenizer.encode("summarize: " + content, return_tensors="pt", max_length=300, truncation=True).to(device)
        with torch.no_grad():
            summary_ids_main = kobart_model.generate(inputs_main, max_length=300, num_beams=4, early_stopping=True)
        summary_main = kobart_tokenizer.decode(summary_ids_main[0], skip_special_tokens=True)
        
        print("🔹 [요약된 뉴스 내용] :", summary_main)

        # ✅ 2. 레퍼런스 뉴스 본문 요약 (KoBART)
        inputs_reference = kobart_tokenizer.encode("summarize: " + referenceContent, return_tensors="pt", max_length=300, truncation=True).to(device)
        with torch.no_grad():
            summary_ids_reference = kobart_model.generate(inputs_reference, max_length=300, num_beams=4, early_stopping=True)
        summary_reference = kobart_tokenizer.decode(summary_ids_reference[0], skip_special_tokens=True)
        
        print("🔹 [요약된 레퍼런스 뉴스 내용] :", summary_reference)

        # ✅ 3. 자연어 추론 (팩트 검증)
        premise = summary_reference  # 요약된 레퍼런스 본문 (전제)
        hypothesis = summary_main  # 요약된 메인 뉴스 본문 (가설)

        # ✅ 4. 입력 데이터 변환 및 예측 (메모리 최적화)
        with torch.no_grad():
            encoded_input = nli_tokenizer(premise, hypothesis, return_tensors="pt", truncation=True, padding=True).to(device)
            output = nli_model(**encoded_input)
            probs = torch.nn.functional.softmax(output.logits, dim=1)

        # ✅ 5. 확률값 기반 판별 로직 개선
        num_classes = probs.shape[1]  # 모델의 실제 출력 크기
        entailment_prob = probs[0][0].item()  # True 확률
        neutral_prob = probs[0][1].item() if num_classes == 3 else 0.5  # 2-class 모델 대비
        contradiction_prob = probs[0][2].item() if num_classes == 3 else 1 - entailment_prob  # 2-class 모델 대비

        print(f"🔹 [유사도] : {entailment_prob:.4f}")        

        return {"similarity_score": entailment_prob}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
