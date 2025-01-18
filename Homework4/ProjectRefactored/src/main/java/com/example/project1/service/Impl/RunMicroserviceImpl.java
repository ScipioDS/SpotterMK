package com.example.project1.service.Impl;

import com.example.project1.service.RunMicroservice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
@Service
public class RunMicroserviceImpl implements RunMicroservice {
    @Override
    public String runSentiment(String saved_company) throws IOException {
        String apiUrl = "http://127.0.0.1:5000/analyze";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set up the connection properties
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true); // To send data in the body of the request

        // Create the JSON payload with the argument
        String jsonInputString = "{\"code\": \"" + saved_company + "\"}";

        // Send the JSON data
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length); // Write the request body
        }

        // Get the response code to check if the request was successful
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the raw JSON response
        String responseBody = response.toString();
        System.out.println("Response Body: " + responseBody);

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        // Example: Access specific fields from the JSON response
        if (jsonResponse.has("result")) {
            String someField = jsonResponse.get("result").asText();
            System.out.println("result: " + someField);
        }

        // Or print the entire parsed JSON tree
        System.out.println("Parsed JSON Response: " + jsonResponse.toString());

        return jsonResponse.get("result").asText();
    }

    @Override
    public String runTechnicalAnalysis(String timeFrame, String saved_company) throws IOException {
        String apiUrl = "http://127.0.0.1:5000/technical";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set up the connection properties
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true); // To send data in the body of the request

        // Create the JSON payload with the argument
        String jsonInputString = "{\"code\": \"" + saved_company + "\", \"timeFrame\": \"" + timeFrame + "\"}";

        // Send the JSON data
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length); // Write the request body
        }

        // Get the response code to check if the request was successful
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the raw JSON response
        String responseBody = response.toString();
        System.out.println("Response Body: " + responseBody);

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        // Example: Access specific fields from the JSON response
        if (jsonResponse.has("result")) {
            String someField = jsonResponse.get("result").asText();
            System.out.println("result: " + someField);
        }

        // Or print the entire parsed JSON tree
        System.out.println("Parsed JSON Response: " + jsonResponse.toString());

        return jsonResponse.get("result").asText();
    }

    @Override
    public String runPredictionAPI(String saved_company) throws IOException {
        String apiUrl = "http://127.0.0.1:5000/prediction";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set up the connection properties
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true); // To send data in the body of the request

        // Create the JSON payload with the argument
        String jsonInputString = "{\"code\": \"" + saved_company + "\"}";

        // Send the JSON data
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length); // Write the request body
        }

        // Get the response code to check if the request was successful
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the raw JSON response
        String responseBody = response.toString();
        System.out.println("Response Body: " + responseBody);

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        // Example: Access specific fields from the JSON response
        if (jsonResponse.has("result")) {
            String someField = jsonResponse.get("result").asText();
            System.out.println("result: " + someField);
        }

        // Or print the entire parsed JSON tree
        System.out.println("Parsed JSON Response: " + jsonResponse.toString());

        return jsonResponse.get("result").asText();
    }
}
