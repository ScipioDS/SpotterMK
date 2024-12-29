from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score
from keras.models import Sequential
from keras.layers import Input, LSTM, Dense, Dropout
import logging
import seaborn as sns
import matplotlib.pyplot as plt
from keras.callbacks import EarlyStopping
from tensorflow.python.ops.losses.losses_impl import mean_squared_error
from tensorflow.python.ops.metrics_impl import root_mean_squared_error

# Logging setup
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")
logger = logging.getLogger(__name__)

# FastAPI app initialization
app = FastAPI()

class HistoricalDataItem(BaseModel):
    date: str
    average_price: float

class HistoricalData(BaseModel):
    data: List[HistoricalDataItem]

@app.post("/predict-next-month-price/")
async def predict_next_month_price_endpoint(historical_data: HistoricalData):
    try:
        logger.info("Received historical data for prediction.")

        # Convert input data to DataFrame
        df = pd.DataFrame([item.dict() for item in historical_data.data])

        if df.empty:
            logger.warning("Empty data received.")
            raise HTTPException(status_code=400, detail="No historical data provided.")

        df['date'] = pd.to_datetime(df['date'])
        df.set_index('date', inplace=True)
        df.sort_index(ascending=True, inplace=True)

        # Preprocess and lag transformation
        df = df[['average_price']].copy()
        lag = 3
        for i in range(lag, 0, -1):
            df[f'Lag_{i}'] = df['average_price'].shift(i)
        df.dropna(inplace=True)

        X, y = df[[f'Lag_{i}' for i in range(lag, 0, -1)]], df['average_price']

        # Train-test split
        train_X, test_X, train_y, test_y = train_test_split(X, y, test_size=0.2, shuffle=False)

        # Scaling
        scaler_X = MinMaxScaler(feature_range=(0, 1))
        scaler_y = MinMaxScaler(feature_range=(0, 1))

        train_X = scaler_X.fit_transform(train_X)
        test_X = scaler_X.transform(test_X)
        logger.info(f"SCALED TRAIN X: {train_X[:5]}")

        train_y = scaler_y.fit_transform(train_y.values.reshape(-1, 1))

        # Reshape for LSTM
        train_X = train_X.reshape(train_X.shape[0], lag, 1)
        test_X = test_X.reshape(test_X.shape[0], lag, 1)

        # Model definition
        model = Sequential([
            Input((train_X.shape[1], train_X.shape[2])),
            LSTM(32, activation="relu", return_sequences=True, kernel_regularizer="l2"),
            LSTM(16, activation="relu", kernel_regularizer="l2"),
            Dense(1, activation="linear")
        ])
        model.compile(loss="mean_squared_error", optimizer="adam", metrics=["mean_squared_error"])
        early_stopping = EarlyStopping(
            monitor="val_loss",
            patience=25,
            restore_best_weights=True
        )
        # Train the model
        history = model.fit(train_X, train_y, validation_split=0.3, epochs=100, batch_size=32, callbacks=[early_stopping], shuffle=False)

        # Plot training history
        plt.figure(figsize=(10, 6))
        sns.lineplot(x=range(len(history.history['loss'])), y=history.history['loss'], label="Training Loss")
        sns.lineplot(x=range(len(history.history['val_loss'])), y=history.history['val_loss'], label="Validation Loss")
        plt.title("Model Loss During Training")
        plt.xlabel("Epochs")
        plt.ylabel("Loss")
        plt.legend()
        plt.grid(True)
        plt.show()

        # Prediction
        pred_y = model.predict(test_X)
        pred_y = scaler_y.inverse_transform(pred_y)

        # Evaluate model
        r2 = r2_score(test_y, pred_y)
        mse = mean_squared_error(test_y.values.reshape(-1, 1), pred_y)
        logger.info(f"R2 Score: {r2}")
        logger.info(f"RMSE: {np.sqrt(mse)}")

        # Extract the last sequence for prediction
        logger.info(f"DF: {df.tail(5)}")
        last_sequence = df[[f'Lag_{i}' for i in range(lag, 0, -1)]].iloc[-1].values

        if len(last_sequence) < lag:
            raise ValueError(f"Insufficient data for the last sequence. Expected {lag} rows, got {len(last_sequence)}.")

        # Fit and scale with a window-specific scaler
        window_size = 30
        recent_window = df.iloc[-window_size:][[f'Lag_{i}' for i in range(lag, 0, -1)]]
        window_scaler = MinMaxScaler(feature_range=(0, 1))
        window_scaler.fit(recent_window)

        # Scale and reshape the last sequence
        last_sequence_df = pd.DataFrame([last_sequence], columns=[f'Lag_{i}' for i in range(lag, 0, -1)])
        last_sequence_scaled = window_scaler.transform(last_sequence_df)
        last_sequence_scaled = last_sequence_scaled.reshape(1, lag, 1)
        logger.info(f"LAST SEQUENCE SCALED: {last_sequence_scaled}")

        # Make the prediction
        forecast_scaled = model.predict(last_sequence_scaled, verbose=0)
        forecast = scaler_y.inverse_transform(forecast_scaled)

        # Log and return the result
        logger.info(f"Predicted next month price: {forecast.flatten()[0]}")
        return {"predicted_next_month_price": float(forecast.flatten()[0])}

    except Exception as e:
        logger.exception("Error during prediction.")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("prediction_api:app", host="localhost", port=8000, reload=True)
