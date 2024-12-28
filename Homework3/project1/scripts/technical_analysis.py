import pandas as pd
import numpy as np
import sys
from sqlalchemy import create_engine
import logging

def fetch_data_from_db(code):
    engine = create_engine('postgresql+psycopg2://postgres:aleks123@localhost:5432/my_database')
    query = f"""
        SELECT h.*   
        FROM companies c
        JOIN historical_data h ON c.id = h.company_id
        WHERE c.company_code='{code}'
        ORDER BY h.date DESC
    """
    df = pd.read_sql(query, engine)
    df = df.rename(columns={
        'date': 'Date',
        'last_transaction_price': 'LastTransaction',
        'max_price': 'Max',
        'min_price': 'Min',
        'average_price': 'Avg',
        'percentage_change': '%Prom',
        'quantity': 'Amount',
        'turnorver_best': 'Best',
        'total_turnover': 'Total'
    })
    df = df.drop(columns=['id','company_id'])
    return df

# Define functions for the oscillators
def macd(data, short_window=12, long_window=26, signal_window=9):
    ema_short = data.ewm(span=short_window, adjust=False).mean()
    ema_long = data.ewm(span=long_window, adjust=False).mean()
    macd_line = ema_short - ema_long
    signal_line = macd_line.ewm(span=signal_window, adjust=False).mean()
    return macd_line, signal_line

def cci(data, high, low, close, window=20):
    tp = (high + low + close) / 3
    ma = tp.rolling(window=window).mean()
    mad = (tp - ma).rolling(window=window).apply(lambda x: np.fabs(x).mean())
    return (tp - ma) / (0.015 * mad)

def williams_r(data, high, low, close, window=14):
    high_max = high.rolling(window=window).max()
    low_min = low.rolling(window=window).min()
    return ((high_max - close) / (high_max - low_min)) * -100

def relative_strength_index(data, window=14):
    delta = data.diff()
    gain = delta.where(delta > 0, 0)
    loss = -delta.where(delta < 0, 0)
    avg_gain = gain.rolling(window=window).mean()
    avg_loss = loss.rolling(window=window).mean()
    rs = avg_gain / avg_loss
    rsi = 100 - (100 / (1 + rs))
    return rsi
def stochastic_oscillator(data, high, low, close, window=14):
    high_max = high.rolling(window=window).max()
    low_min = low.rolling(window=window).min()
    stochastic = ((close - low_min) / (high_max - low_min)) * 100
    return stochastic
def simple_moving_average(data, window):
    return data.rolling(window=window).mean()

def exponential_moving_average(data, span):
    return data.ewm(span=span, adjust=False).mean()

def weighted_moving_average(data, window):
    weights = np.arange(1, window + 1)
    return data.rolling(window=window).apply(lambda prices: np.dot(prices, weights) / weights.sum(), raw=True)

def hull_moving_average(data, window):
    wma_half = weighted_moving_average(data, window // 2)
    wma_full = weighted_moving_average(data, window)
    diff = 2 * wma_half - wma_full
    return weighted_moving_average(diff, int(np.sqrt(window)))

def moving_average_envelopes(data, window, percentage=2):
    sma = simple_moving_average(data, window)
    upper_band = sma * (1 + percentage / 100)
    lower_band = sma * (1 - percentage / 100)
    return upper_band, lower_band


def moving_average_decision(row, short_ma, long_ma):
    if row[short_ma] > row[long_ma]:
        return "Buy"
    elif row[short_ma] < row[long_ma]:
        return "Sell"
    else:
        return "Hold"
def envelope_decision(row, price_col, upper_band, lower_band):
    if row[price_col] > row[upper_band]:
        return "Sell"
    elif row[price_col] < row[lower_band]:
        return "Buy"
    else:
        return "Hold"
def trade_decision(row):
    if row['RSI_14'] > 70 or row['Stochastic'] > 80 or row['Williams_%R'] > -20:
        return "Sell"
    elif row['RSI_14'] < 30 or row['Stochastic'] < 20 or row['Williams_%R'] < -80:
        return "Buy"
    else:
        return "Hold"

def moving_average_envelopes(data, window, percentage=2):
    sma = data.rolling(window=window).mean()
    upper_band = sma * (1 + percentage / 100)
    lower_band = sma * (1 - percentage / 100)
    return upper_band, lower_band

code = sys.argv[1:][0]
# print(f"Code: {code}")
df=fetch_data_from_db(code)
# print(df.head())
# print(df.columns)
# print(df.info())
# df['LastTransaction'] = df['LastTransaction'].str.replace('.', '').str.replace(',', '.').astype(float)
# df['Max'] = df['Max'].replace({0: None}).apply(lambda x: str(x).replace('.', '').replace(',', '.') if pd.notnull(x) else None)
# df['Min'] = df['Min'].replace({0: None}).apply(lambda x: str(x).replace('.', '').replace(',', '.') if pd.notnull(x) else None)
# df['Max'] = pd.to_numeric(df['Max'], errors='coerce')
# df['Min'] = pd.to_numeric(df['Min'], errors='coerce')
num_days=int(sys.argv[1:][1])
price_data = df['LastTransaction']
high = df['Max']
low = df['Min']
close = price_data
df['RSI_14'] = relative_strength_index(price_data, 14)
df['Stochastic'] = stochastic_oscillator(df, df['Max'], df['Min'], df['LastTransaction'], window=14)
df['CCI'] = cci(price_data, high, low, close)
df['Williams_%R'] = williams_r(price_data, high, low, close)
df['rolling_5']=df['LastTransaction'].rolling(5).mean()
df['SMA_20'] = simple_moving_average(price_data, 20)
df['EMA_20'] = exponential_moving_average(price_data, 20)
df['EMA_50'] = exponential_moving_average(price_data, 50)
df['WMA_20'] = weighted_moving_average(price_data, 20)
df['HMA_20'] = hull_moving_average(price_data, 20)
df['MAE_Upper'], df['MAE_Lower'] = moving_average_envelopes(df['LastTransaction'], window=20, percentage=2)
df['Trade_Decision_MA'] = df.apply(moving_average_decision, axis=1, short_ma='EMA_20', long_ma='EMA_50')
df['Trade_Decision_Envelope'] = df.apply(
    envelope_decision,
    axis=1,
    price_col='LastTransaction',
    upper_band='MAE_Upper',
    lower_band='MAE_Lower'
)
df['Trade_Decision'] = df.apply(trade_decision, axis=1)

combined_decisions = pd.concat([df['Trade_Decision_MA'], df['Trade_Decision_Envelope'], df['Trade_Decision']])


majority_vote_combined = combined_decisions.mode()[0]


print(f"{majority_vote_combined}")

