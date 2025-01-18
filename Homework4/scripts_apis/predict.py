import pandas as pd
from flask import jsonify, request, app, Flask
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score
from keras.models import Sequential
from keras.layers import Input, LSTM, Dense, Dropout
from keras.callbacks import EarlyStopping
from tensorflow.python.ops.losses.losses_impl import mean_squared_error
app = Flask(__name__)
def predict_next_month_price_endpoint(code):
        # Convert input data to DataFrame
        df = pd.read_csv("C:\\Users\\Skipio\\Desktop\\ScipioDS SpotterMK main Homework3-Project\\scripts\\reports\\" + code + ".csv")

        df['Date'] = pd.to_datetime(df['Date'], format="%d.%m.%Y")
        df.set_index('Date', inplace=True)
        df.sort_index(ascending=True, inplace=True)

        # Preprocess and lag transformation
        df = df[['Avg']].copy()
        df['Avg'] = df['Avg'].str.replace('.', '', regex=False).str.replace(',', '.', regex=False)

        # Convert the column to numeric
        df['Avg'] = pd.to_numeric(df['Avg'])
        lag = 3
        for i in range(lag, 0, -1):
            df[f'Lag_{i}'] = df['Avg'].shift(i)
        df.dropna(inplace=True)

        X, y = df[[f'Lag_{i}' for i in range(lag, 0, -1)]], df['Avg']

        # Train-test split
        train_X, test_X, train_y, test_y = train_test_split(X, y, test_size=0.2, shuffle=False)

        # Scaling
        scaler_X = MinMaxScaler(feature_range=(0, 1))
        scaler_y = MinMaxScaler(feature_range=(0, 1))

        train_X = scaler_X.fit_transform(train_X)
        test_X = scaler_X.transform(test_X)

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

        # Prediction
        pred_y = model.predict(test_X)
        pred_y = scaler_y.inverse_transform(pred_y)

        # Evaluate model
        r2 = r2_score(test_y, pred_y)
        mse = mean_squared_error(test_y.values.reshape(-1, 1), pred_y)

        # Extract the last sequence for prediction
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

        # Make the prediction
        forecast_scaled = model.predict(last_sequence_scaled, verbose=0)
        forecast = scaler_y.inverse_transform(forecast_scaled)

        # Log and return the result
        return float(forecast.flatten()[0])

@app.route('/prediction', methods=['POST'])
def predict():
    data = request.json
    code = data.get('code')
    if not code:
        return jsonify({"error": "Code is required"}), 400
    string = f"NEXT PRICE: {predict_next_month_price_endpoint(code)}"
    return jsonify({"result": string}), 200

# Define the API endpoint
if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)