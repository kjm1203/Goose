from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from goose3 import Goose
import logging

# FastAPI 인스턴스 생성
app = FastAPI()
goose = Goose({'browser_user_agent': 'Mozilla', 'enable_image_fetching': True})  # ✅ 대표 이미지 크롤링 활성화

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")


# 요청 데이터 모델 정의
class NewsRequest(BaseModel):
    url: str

@app.post("/extract")
async def extract_news(request: NewsRequest):
    """
    🔹 Spring Boot에서 보낸 뉴스 URL을 받아 Goose3로 크롤링하여 반환하는 API
    """
    logging.info(f"🔍 [FastAPI] 크롤링 요청: {request.url}")
    
    try:
        article = goose.extract(request.url)
        result = {
            "title": article.title,
            "text": article.cleaned_text,
            "image": article.top_image  # ✅ 대표 이미지 추출
        }
        
        # 크롤링 결과 출력
        logging.info(f"✅ [FastAPI] 크롤링 완료 - 전체: {result}")
        logging.info(f"✅ [FastAPI] 크롤링 완료 - 제목: {result['title']}")
        logging.info(f"✅ [FastAPI] 크롤링 완료 - 내용: {result['text']}")
        logging.info(f"✅ [FastAPI] 대표 이미지: {result['image']}")
        
        return result
    
    except Exception as e:
        logging.error(f"❌ [FastAPI] 크롤링 실패: {str(e)}")
        raise HTTPException(status_code=500, detail=f"크롤링 실패: {str(e)}")

# FastAPI 서버 실행 (로컬 테스트용)
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000, log_level="info")
