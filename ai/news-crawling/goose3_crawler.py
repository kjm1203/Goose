import logging
from goose3 import Goose

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

class Goose3Crawler:
    def __init__(self):
        self.goose = Goose({'enable_image_fetching': True})

    def extract_article(self, url):
        """
        🔹 뉴스 URL에서 본문 및 대표 이미지 추출 (Spring Boot 요청 시 로그 출력)
        """
        print(f"🔍 [Goose3] 크롤링 요청: {url}")
        logging.info(f"🔍 [Goose3] 크롤링 요청: {url}")

        try:
            article = self.goose.extract(url=url)
            print(f"✅ [Goose3] 크롤링 성공: {url}")
            logging.info(f"✅ [Goose3] 크롤링 성공: {url}")

            return {
                "title": article.title,
                "text": article.cleaned_text,  
                "image": article.top_image.src
            }
        except Exception as e:
            print(f"❌ [Goose3] 크롤링 실패: {url} | 오류: {str(e)}")
            logging.error(f"❌ [Goose3] 크롤링 실패: {url} | 오류: {str(e)}")
            return None

# 테스트 실행
if __name__ == "__main__":
    test_url = "https://n.news.naver.com/mnews/article/214/0001402995?sid=100"

    crawler = Goose3Crawler()
    result = crawler.extract_article(test_url)

    if result:
        print(f"✅ [테스트] 크롤링 결과: {result['title']}")
        logging.info(f"✅ [테스트] 크롤링 결과: {result['title']}")
    else:
        print("❌ [테스트] 크롤링 실패")
        logging.error("❌ [테스트] 크롤링 실패")
