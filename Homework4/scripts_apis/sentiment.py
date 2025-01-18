import sys
import pandas as pd
from bs4 import BeautifulSoup
import requests
from transformers import pipeline
from flask import Flask, request, jsonify
app = Flask(__name__)

# Define the scripts_apis analysis function
def sentiment_analysis(name):
    url = "https://www.mse.mk"
    sentiment_pipeline = pipeline("scripts_apis-analysis")
    for page in range(1, 3):
        r = requests.get(url + "/mk/news/latest/" + str(page))
        soup = BeautifulSoup(r.text, "html.parser")
        links_section = soup.find("div", {"id": "news-content"})

        if not links_section:
            continue

        links = links_section.find_all("a")
        labels = []
        for i, link in enumerate(links):
            if i % 2 == 0:  # Adjust this logic if necessary
                try:
                    r = requests.get(url + link['href'])
                    soup = BeautifulSoup(r.text, "html.parser")
                    test = ""

                    content_section = soup.find("div", {"id": "content"})
                    if not content_section:
                        continue

                    for tag in content_section.find_all("p"):
                        test += tag.text + "\n"

                    if name in test:
                        sentiment_result = sentiment_pipeline(test)
                        labels.append(sentiment_result)
                except Exception as e:
                    print(f"Error processing link {link['href']}: {e}")
        if len(labels) > 0:
            return labels
    return "NOT FOUND"

@app.route('/analyze', methods=['POST'])
def analyze():
    data = request.json
    code = data.get('code')
    if not code:
        return jsonify({"error": "Code is required"}), 400

    try:
        df = pd.read_csv("NameScrape.csv")
        result = df.loc[df['code'] == code]['name'].iloc[0]
        sent_res = sentiment_analysis(result)

        if sent_res == "NOT FOUND":
            return jsonify({"result": "NOT FOUND"}), 200

        pos, neg = 0, 0
        for res in sent_res:
            if res[0]['label'] == "NEGATIVE":
                neg += 1
            else:
                pos += 1

        decision = "SELL" if neg > pos else "BUY"
        return jsonify({"result": decision, "details": sent_res}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)