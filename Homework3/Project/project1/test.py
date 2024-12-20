import sys

import pandas as pd
from bs4 import BeautifulSoup
import requests
from transformers import pipeline


def sentiment_analysis(name):
    url = "https://www.mse.mk"
    sentiment_pipeline = pipeline("sentiment-analysis")
    for page in range(1, 3):
        r = requests.get(url + "/mk/news/latest/" + str(page))
        soup = BeautifulSoup(r.text, "html.parser")
        links_section = soup.find("div", {"id": "news-content"})

        if not links_section:
            print(f"No news content found on page {page}.")
            continue

        links = links_section.find_all("a")
        i = 0

        labels = []
        for link in links:
            if i % 2 == 0:  # Adjust this logic if necessary
                try:
                    print(f"Processing link: {link['href']}")
                    r = requests.get(url + link['href'])
                    soup = BeautifulSoup(r.text, "html.parser")
                    test = ""

                    content_section = soup.find("div", {"id": "content"})
                    if not content_section:
                        print(f"No content found for link: {link['href']}")
                        continue

                    for tag in content_section.find_all("p"):
                        test += tag.text + "\n"

                    if name in test:
                        print(f"Found name in text: {name}")
                        print(f"Text: {test[:500]}...")  # Print only the first 500 characters
                        sentiment_result = sentiment_pipeline(test)
                        print(f"Sentiment: {sentiment_result}")
                        print(f"URL: {link['href']}")
                        labels.append(sentiment_result)
                except Exception as e:
                    print(f"Error processing link {link['href']}: {e}")
            i += 1
        if len(labels) > 0:
            return labels
        else:
            return "NOT FOUND"


if __name__ == "__main__":
    df = pd.read_csv("NameScrape.csv",encoding="utf-8")
    sys.stdout.reconfigure(encoding='utf-8')
    sys.stderr.reconfigure(encoding='utf-8')
    sys.stdin.reconfigure(encoding='utf-8')
    code = sys.argv[1:][0]
    # code = "kmb"
    print(code)
    result = df.loc[df['code'] == code]['name'].iloc[0]
    sent_res = sentiment_analysis(result)
    print("------RESULT------")
    pos = 0
    neg = 0
    if sent_res == "NOT FOUND":
        print("NOT FOUND")
    else:
        for res in sent_res:
            if res[0]['label'] == "NEGATIVE":
                neg+=1
            else:
                pos+=1
        if neg > pos:
            print("SELL")
        else:
            print("BUY")