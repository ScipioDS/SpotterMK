from datetime import datetime, timedelta

import pandas as pd
import requests
from bs4 import BeautifulSoup
import re
from sklearn.impute import SimpleImputer

if __name__ == '__main__':
    url = "https://www.mse.mk/mk/stats/symbolhistory/kmb"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')
    select_element = soup.find("select", id="Code")
    constant_imputer = SimpleImputer(strategy='constant', fill_value=0)
    codes = [option.text.lower() for option in select_element.find_all("option") if not re.search(r'\d', option.text)]
    for code in codes:
        df = pd.read_csv(f"../reports/{code}.csv")
        df['Min'] = constant_imputer.fit_transform([df['Min']])[0]
        df['Max'] = constant_imputer.fit_transform([df['Max']])[0]
        df.to_csv(f"../reports/{code}.csv")
