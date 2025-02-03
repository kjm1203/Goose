from goose3 import Goose
import requests
import json

class Goose3Crawler:
    def __init__(self):
        self.goose = Goose()

    def extract_article(self, url):
        """
        🔹 뉴스 URL에서 본문 및 대표 이미지 추출
        """
        try:
            article = self.goose.extract(url)
            return {
                "title": article.title,
                "text": article.cleaned_text,
                "image": article.top_image
            }
        except Exception as e:
            print(f"❌ [ERROR] {url} 크롤링 실패: {str(e)}")
            return None

# 테스트 실행
if __name__ == "__main__":
    test_url = "https://n.news.naver.com/mnews/article/214/0001402995?sid=100"
    
    crawler = Goose3Crawler()
    result = crawler.extract_article(test_url)

    if result:
        print("✅ 제목:", result["title"])
        print("✅ 본문:", result["text"][:500], "...")  # 본문의 앞부분만 출력
        print("✅ 대표 이미지:", result["image"])
    else:
        print("❌ 크롤링 실패")
