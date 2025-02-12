# ✅ Python 3.9 기반 이미지 사용
FROM python:3.9

# ✅ 작업 디렉토리 생성
WORKDIR /app

# ✅ 시스템 패키지 업데이트 & 필수 라이브러리 설치 (KoBART & Transformers 최적화)
RUN apt-get update && apt-get install -y libgomp1

# ✅ 필요한 파일 복사
COPY . /app

# ✅ 가상 환경 활성화 및 의존성 설치
RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# ✅ spaCy 한국어 모델 다운로드 (필수)
RUN python -m spacy download ko-core-news-sm

# ✅ 실시간 로그 출력을 위한 환경 변수 설정
ENV PYTHONUNBUFFERED=1

# ✅ FastAPI 애플리케이션 실행 (uvicorn)
CMD ["uvicorn", "title_compare_contents:app", "--host", "0.0.0.0", "--port", "5057"]
