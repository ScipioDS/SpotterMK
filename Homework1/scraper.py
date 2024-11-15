from datetime import datetime, timedelta
import pandas as pd
import requests
from bs4 import BeautifulSoup
import re
import time  # Importing time module for timing

NUM_OF_YEARS = 10

def fetch_historic_data_bs4(ticker):
    master_url = f"https://www.mse.mk/mk/stats/symbolhistory/{ticker}"
    historic_data = []
    to_date = datetime.now()

    for _ in range(NUM_OF_YEARS):
        from_date = to_date - timedelta(days=364)
        params = {
            "FromDate": from_date.strftime("%m/%d/%Y"),
            "ToDate": to_date.strftime("%m/%d/%Y"),  # Fixed typo in the date format
        }
        response = requests.get(master_url, params=params)
        html = BeautifulSoup(response.text, 'html.parser')

        table = html.select_one('#resultsTable > tbody')
        if table:
            rows = table.find_all('tr')
            for row in rows:
                data_row = [cell.text.strip() for cell in row.find_all('td')]
                historic_data.append(data_row)

        to_date = from_date  # Update to_date for the next iteration
    return historic_data

if __name__ == "__main__":
    start_time = time.time()  # Record the start time

    url = "https://www.mse.mk/mk/stats/symbolhistory/kmb"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    select_element = soup.find("select", id="Code")
    codes = [option.text.lower() for option in select_element.find_all("option") if not re.search(r'\d', option.text)]

    for code in codes:
        print(f"Fetching data for: {code}")
        df = pd.DataFrame(fetch_historic_data_bs4(f'{code}'), columns=["Date", "LastTransaction", "Max", "Min", "Avg", "%Prom", "Amount", "BEST", "Total"])
        df.to_csv(f'{code}.csv', index=False)
        print(f"Data for {code} saved to {code}.csv")

    end_time = time.time()
    elapsed_time = end_time - start_time

    print(f"Execution completed in {elapsed_time:.2f} seconds")
