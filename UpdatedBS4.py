from datetime import datetime, timedelta, date

import pandas as pd
import requests
from bs4 import BeautifulSoup
import TickerScrapeBS4

NUM_OF_YEARS = 10


def update_historic_data_bs4(ticker, from_date_given):
    master_url = f"https://www.mse.mk/en/stats/symbolhistory/{ticker}"
    historic_data = []
    from_date = from_date_given
    to_date = date.today()
    params = {
        "FromDate": from_date.strftime("%m/%d/%Y"),
        "ToDate": to_date.strftime("%m/%m/%Y"),
    }
    response = requests.get(master_url, params=params)
    html = BeautifulSoup(response.text, 'html.parser')

    table = html.select_one('#resultsTable > tbody')
    if table:
        rows = table.find_all('tr')
        for row in rows:
            data_row = [cell.text.strip() for cell in row.find_all('td')]
            historic_data.append(data_row)

    return historic_data


if __name__ == "__main__":
    with open("kmb.csv", "r", encoding="utf-8", errors="ignore") as scraped:
        final_line = scraped.readlines()[1]
    nums = final_line.split(",")[0].split("/")
    day = int(nums[1])
    month = int(nums[0])
    year = int(nums[2])
    sdate = date(year, month, day)

    df1 = pd.DataFrame(update_historic_data_bs4("kmb", sdate), columns=["Date", "LastTransaction", "Max", "Min", "Avg", "%Prom", "Amount", "BEST", "Total"])
    df2 = pd.read_csv("kmb.csv", encoding="utf")
    df3 = pd.concat([df1, df2], ignore_index=True, sort=False)
    df3.to_csv("kmb.csv", index=False)
