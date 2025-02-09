# 공식 Python 3.9 이미지 사용
FROM python:3.9

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일 복사
COPY requirements.txt ./
COPY news_keyword_extraction.py ./

# 패키지 설치
RUN pip install --upgrade pip && pip install -r requirements.txt

# 한국어 모델 다운로드
RUN python -m spacy download ko_core_news_sm

# FastAPI 실행
CMD ["uvicorn", "news_keyword_extraction:app", "--host", "0.0.0.0", "--port", "5053", "--reload"]
