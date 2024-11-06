from datetime import datetime, timedelta

import pandas as pd
import requests
from bs4 import BeautifulSoup
import re

NUM_OF_YEARS = 10



def fetch_historic_data_bs4(ticker):
    master_url = f"https://www.mse.mk/mk/stats/symbolhistory/{ticker}"
    historic_data = []
    date_to = datetime.now()

    for _ in range(NUM_OF_YEARS):
        date_from = date_to - timedelta(days=364)
        params = {
            "FromDate": date_from.strftime("%d.%m.%Y"),
            "ToDate": date_to.strftime("%d.%m.%Y"),
        }
        response = requests.get(master_url, params=params)
        html = BeautifulSoup(response.text, 'html.parser')

        table = html.select_one('#resultsTable > tbody')
        if table:
            rows = table.find_all('tr')
            for row in rows:
                data_row = [cell.text.strip() for cell in row.find_all('td')]
                historic_data.append(data_row)

        date_to = date_from
    return historic_data


if __name__ == "__main__":
    url = "https://www.mse.mk/mk/stats/symbolhistory/kmb"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')
    select_element = soup.find("select", id="Code")
    codes = [option.text.lower() for option in select_element.find_all("option") if not re.search(r'\d', option.text)]
    for code in codes:
        df = pd.DataFrame(fetch_historic_data_bs4(f'{code}'), columns=["Date", "LastTransaction", "Max", "Min", "Avg", "%Prom", "Amount", "BEST", "Total"])
        df.to_csv(f'{code}.csv',index=False)
    print(df)