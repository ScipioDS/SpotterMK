import sqlite3
import pandas as pd
import requests
from bs4 import BeautifulSoup
import re

url = "https://www.mse.mk/mk/stats/symbolhistory/kmb"
response = requests.get(url)
soup = BeautifulSoup(response.text, 'html.parser')
select_element = soup.find("select", id="Code")
codes = [option.text.lower() for option in select_element.find_all("option") if not re.search(r'\d', option.text)]
for code in codes:
    df = pd.read_csv(f"../reports/{code}.csv")
    df = df.drop(columns=['Unnamed: 0'])
    df = df.rename(columns={'%Prom': 'Prom'})
    conn = sqlite3.connect('../rp.db')
    cursor = conn.cursor()
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS code (
        id INTEGER PRIMARY KEY,
        Date TEXT,
        LastTransaction TEXT,
        Max TEXT,
        Min TEXT,
        Avg TEXT,
        Prom TEXT,
        Amount INTEGER,
        BEST REAL,
        Total REAL
    )
    ''')
    df.to_sql(code, conn, if_exists='append', index=False)
    conn.commit()
    conn.close()
