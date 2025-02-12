# ✅ Python 3.9 기반 이미지 사용
FROM python:3.9

# ✅ 작업 디렉토리 생성
WORKDIR /app

# ✅ 시스템 패키지 업데이트 및 필수 라이브러리 설치
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    g++ \
    gcc \
    make \
    curl \
    libgomp1 \
    && apt-get clean

# ✅ JAVA_HOME 환경 변수 설정
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# ✅ requirements.txt 먼저 복사하여 종속성 설치 (최적화)
COPY requirements.txt /app/requirements.txt
RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# ✅ spaCy 한국어 모델 다운로드 (spaCy 설치 이후 실행)
RUN python -m spacy download ko_core_news_sm

# ✅ 전체 소스 복사
COPY . /app

# ✅ FastAPI 실행
CMD ["uvicorn", "content_compare_contents:app", "--host", "0.0.0.0", "--port", "5058"]
