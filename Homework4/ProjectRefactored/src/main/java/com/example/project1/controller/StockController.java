package com.example.project1.controller;

import com.example.project1.entity.HistoricalDataEntity;
import com.example.project1.service.DatabaseConnector;
import com.example.project1.service.RunMicroservice;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StockController {
    private final DatabaseConnector databaseConnector;
    private final RunMicroservice runMicroservice;
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    private String saved_company = "";

    @GetMapping("/about")
    public String getAbout(){
        return "about";
    }

    @GetMapping({"/","/home"})
    public String getHome(){
        return "landing-page";
    }

    @GetMapping("/exploreCompany")
    @ResponseBody
    public Map<String, Object> exploreCompany(@RequestParam(name = "companyId") String companyId, Model model) {
        Map<String, Object> response = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        for (HistoricalDataEntity historicalData : databaseConnector.getHistoricalData(companyId)) {
            dates.add(historicalData.getDate().toString());
            prices.add(historicalData.getLastTransactionPrice());
        }

        saved_company=companyId;

        response.put("dates", dates);
        response.put("prices", prices);
        response.put("status", "success");
        return response;
    }
    @GetMapping("/explore")
    public String getStocksPage(Model model) {
        logger.info("Rendering index page with company list.");
        model.addAttribute("companies", databaseConnector.findAllNames());
        return "stock_page_update";
    }

    @PostMapping("/runPrediction")
    @ResponseBody
    public String runPredictionAPI() throws IOException, InterruptedException {
        return this.runMicroservice.runPredictionAPI(saved_company);
    }
    @PostMapping("/runPythonScript")
    @ResponseBody
    public String runPythonScript(@RequestBody StockController.AnalysisRequest request) throws IOException, InterruptedException {
        String analysisType = request.getAnalysisType();
        String timeframe = request.getTimeframe();
        String output = "";

        if ("technical".equals(analysisType)) {
            if ("1_day".equals(timeframe)) {
                timeframe = "1";
            } else if ("1_week".equals(timeframe)) {
                timeframe = "7";
            } else if ("1_month".equals(timeframe)) {
                timeframe = "30";
            }
            output = runMicroservice.runTechnicalAnalysis(timeframe, saved_company);
        } else {
            output = runMicroservice.runSentiment(saved_company);
        }

        return output;
    }

    public static class AnalysisRequest {
        private String analysisType;
        @JsonProperty("timeframe")
        private String timeframe;

        // Getters and setters
        public String getAnalysisType() {
            return analysisType;
        }

        public void setAnalysisType(String analysisType) {
            this.analysisType = analysisType;
        }

        public String getTimeframe() {
            return timeframe;
        }

        public void setTimeframe(String timeframe) {
            this.timeframe = timeframe;
        }
    }

}
