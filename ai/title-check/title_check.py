from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import torch
from transformers import AutoModelForSequenceClassification, AutoTokenizer
from typing import List

app = FastAPI()

# ✅ KLUE-RoBERTa 모델 로드 (자연어 추론)
nli_model = AutoModelForSequenceClassification.from_pretrained("klue/roberta-large")
nli_tokenizer = AutoTokenizer.from_pretrained("klue/roberta-large")

class TitleCheckRequest(BaseModel):
    title: str
    reference_contents: List[str]

@app.post("/titlecheck")
async def title_check(request: TitleCheckRequest):
    try:
        title = request.title
        reference_contents = request.reference_contents

        if not reference_contents:
            return {"title": title, "factcheck_result": "No References Available"}

        contradiction_count = 0
        entailment_count = 0
        neutral_count = 0

        for content in reference_contents:
            encoded_input = nli_tokenizer(content, title, return_tensors="pt", truncation=True, padding=True)
            output = nli_model(**encoded_input)
            prediction = torch.argmax(output.logits, dim=1).item()

            if prediction == 0:
                entailment_count += 1
            elif prediction == 1:
                neutral_count += 1
            elif prediction == 2:
                contradiction_count += 1

        # 🔹 신뢰성 판단
        total = entailment_count + neutral_count + contradiction_count
        if total == 0:
            result = "No Relevant Data"
        elif contradiction_count / total > 0.5:
            result = "False"
        elif entailment_count / total > 0.5:
            result = "True"
        else:
            result = "Partially True"

        return {"title": title, "factcheck_result": result}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
