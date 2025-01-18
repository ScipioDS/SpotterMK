package com.example.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface RunMicroservice {
    public String runSentiment(String saved_company) throws IOException;
    public String runTechnicalAnalysis(String timeFrame, String saved_company) throws IOException;
    public String runPredictionAPI(String saved_company) throws IOException;
}
