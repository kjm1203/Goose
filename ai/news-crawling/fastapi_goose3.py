from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from goose3 import Goose

# FastAPI 인스턴스 생성
app = FastAPI()
goose = Goose()

# 요청 데이터 모델 정의
class NewsRequest(BaseModel):
    url: str

@app.post("/extract")
async def extract_news(request: NewsRequest):
    """
    🔹 Spring Boot에서 보낸 뉴스 URL을 받아 Goose3로 크롤링하여 반환하는 API
    """
    try:
        article = goose.extract(request.url)
        return {
            "title": article.title,
            "text": article.cleaned_text,
            "image": article.top_image
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"크롤링 실패: {str(e)}")

# FastAPI 서버 실행 (로컬 테스트용)
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000, log_level="info")
