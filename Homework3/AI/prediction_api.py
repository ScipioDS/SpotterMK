from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
from sklearn.preprocessing import MinMaxScaler
import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout, Input
from tensorflow.keras.callbacks import EarlyStopping
import logging
import matplotlib.pyplot as plt
from tensorflow.keras.regularizers import l2
import seaborn as sns
from sklearn.metrics import mean_squared_error, r2_score

logging.basicConfig(
    level=logging.DEBUG,
    format="%(asctime)s - %(levelname)s - %(message)s",
    handlers=[
        logging.StreamHandler()  # Log to console
    ]
)

logger = logging.getLogger(__name__)

app = FastAPI()

class HistoricalDataItem(BaseModel):
    date: str
    average_price: float

class HistoricalData(BaseModel):
    data: List[HistoricalDataItem]

def model_performance_plot(history, output_file):
    plt.figure(figsize=(10, 6))
    sns.lineplot(data=history.history['loss'][1:], label='Training Loss')
    sns.lineplot(data=history.history['val_loss'][1:], label='Validation Loss')
    plt.title('Model Loss During Training')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.legend()
    plt.grid(True)

    # plt.savefig(output_file, dpi=300)
    plt.show()

def predict_next_month_price(historical_data: pd.DataFrame) -> float:
    try:
        logger.info("Starting prediction process.")
        logger.debug(f"Initial data:\n{historical_data.head()}")

        # Parsing and sorting the data
        historical_data['date'] = pd.to_datetime(historical_data['date'])
        historical_data.set_index('date', inplace=True)
        historical_data.sort_index(ascending=True, inplace=True)

        # Resample and interpolate missing data
        historical_data = historical_data.resample('W').mean()
        historical_data = historical_data.interpolate(method='linear')
        logger.debug(f"Historical data resampled: {historical_data.head(10)}")

        prices = historical_data['average_price'].values.reshape(-1, 1)
        logger.debug(f"Prices array shape: {prices.shape}")

        # Preparing sequences for training
        sequence_length = 4

        if len(prices) <= sequence_length:
            logger.error(
                f"Insufficient data: {len(prices)} data points provided, but at least {sequence_length + 1} are required.")
            raise ValueError(
                f"Insufficient data. At least {sequence_length + 1} data points are required for prediction.")

        x, y = [], []
        for i in range(len(prices) - sequence_length):
            x.append(prices[i:i + sequence_length])
            y.append(prices[i + sequence_length])
        x, y = np.array(x), np.array(y)
        logger.debug(f"Prepared sequences - x shape: {x.shape}, y shape: {y.shape}")

        # Scaling the data
        scaler = MinMaxScaler()
        x = scaler.fit_transform(x.reshape(-1, 1)).reshape(x.shape[0], x.shape[1], 1)
        y = scaler.fit_transform(y.reshape(-1, 1)).flatten()
        logger.debug(f"Scaled x shape: {x.shape}, Scaled y shape: {y.shape}")

        # Building the LSTM model
        logger.info("Building the LSTM model.")
        model = Sequential([
            LSTM(16, activation='relu', return_sequences=True, kernel_regularizer=l2(0.01), input_shape=(sequence_length, 1)),
            Dropout(0.2),
            LSTM(8, activation='relu', kernel_regularizer=l2(0.01)),
            Dense(1, activation='linear')
        ])
        model.compile(
            loss="mean_squared_error",
            optimizer="adam",
            metrics=["mean_squared_error"]
        )

        # Training the model
        logger.info("Starting model training.")
        early_stopping = EarlyStopping(monitor='val_loss', patience=10, restore_best_weights=True)
        history = model.fit(x, y, epochs=30, batch_size=4, validation_split=0.3, verbose=0, callbacks=[early_stopping])
        logger.info("Model training completed.")

        # Logging model performance
        model_performance_plot(history, output_file="C:\\Users\\User\\Desktop\\AI\\model_performance.png")
        logger.info("Model performance plot generated.")

        # Validation
        val_predicted = model.predict(x[int(len(x) * 0.7):], verbose=0)
        val_actual = y[int(len(y) * 0.7):]
        mse = mean_squared_error(val_actual, val_predicted)
        logger.info(f"Validation Mean Squared Error (MSE): {mse:.4f}")

        # Forecasting the next month price
        last_sequence = prices[-sequence_length:]
        logger.debug(f"Last sequence shape: {last_sequence.shape}")
        last_sequence = scaler.transform(last_sequence.reshape(-1, 1))
        last_sequence = np.expand_dims(last_sequence, axis=0)
        logger.debug(f"Transformed last sequence shape: {last_sequence.shape}")

        forecast_scaled = model.predict(last_sequence, verbose=0)
        forecast = scaler.inverse_transform(forecast_scaled)
        logger.info(f"Predicted price: {forecast.flatten()[0]}")

        return forecast.flatten()[0]

    except Exception as e:
        logger.exception("Error during prediction.")
        raise

# FastAPI endpoint
@app.post("/predict-next-month-price/")
async def predict_next_month_price_endpoint(historical_data: HistoricalData):
    try:
        logger.info("Received historical data for prediction.")
        data = pd.DataFrame([item.dict() for item in historical_data.data])

        if data.empty:
            logger.warning("Empty data received.")
            raise HTTPException(status_code=400, detail="No historical data provided.")

        predicted_price = predict_next_month_price(data)
        logger.info("Prediction successful.")
        return {"predicted_next_month_price": float(predicted_price)}

    except HTTPException as http_err:
        logger.error("HTTP error during prediction: %s", http_err.detail)
        raise http_err
    except Exception as e:
        logger.exception("Unexpected error during prediction.")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("prediction_api:app", host="localhost", port=8000, reload=True)

