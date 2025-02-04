from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from newspaper import Article
import logging

app = FastAPI()

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

class NewsRequest(BaseModel):
    url: str

@app.post("/extract")
async def extract_news(request: NewsRequest):
    logging.info(f"🔍 [FastAPI] 크롤링 요청: {request.url}")
    
    try:
        article = Article(request.url)
        article.download()
        article.parse()
        
        # ✅ 크롤링된 본문 내용 길이 확인
        logging.info(f"✅ [FastAPI] 크롤링된 본문 길이: {len(article.text)}")

        result = {
            "title": article.title,
            "text": article.text,  # 전체 본문을 저장
            "image": article.top_image
        }

        logging.info(f"✅ [FastAPI] 크롤링 완료 - 제목: {result['title']}")
        logging.info(f"✅ [FastAPI] 본문 (일부): {result['text'][:500]}...")  # 앞부분 500자만 출력
        logging.info(f"✅ [FastAPI] 대표 이미지: {result['image']}")

        return result
    
    except Exception as e:
        logging.error(f"❌ [FastAPI] 크롤링 실패: {str(e)}")
        raise HTTPException(status_code=500, detail=f"크롤링 실패: {str(e)}")


# FastAPI 서버 실행
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5000, log_level="info")