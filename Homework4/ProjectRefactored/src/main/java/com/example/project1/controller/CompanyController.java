//package com.example.project1.controller;
//
//import com.example.project1.entity.CompanyEntity;
//import com.example.project1.entity.HistoricalDataEntity;
//import com.example.project1.service.PricePredictionService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Controller
//@RequiredArgsConstructor
//public class CompanyController {
//
//    private final CompanyService companyService;
//    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
//    private final PricePredictionService pricePredictionService;
//    private String saved_company = "";
//
//    @GetMapping("/")
//    public String getIndexPage(Model model) {
//        logger.info("Rendering index page with company list.");
//        model.addAttribute("companies", companyService.findAll());
//        return "stock_page";
//    }
//
//    @GetMapping("/company")
//    @ResponseBody
//    public Map<String, Object> getCompanyData(@RequestParam(name = "companyId") Long companyId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            logger.info("Fetching data for company ID: {}", companyId);
//            CompanyEntity company = companyService.findById(companyId);
//            saved_company=company.getCompanyCode();
//
//            response.put("companyCode", company.getCompanyCode());
//
//            if (company.getLastUpdated() != null) {
//                response.put("lastUpdated", company.getLastUpdated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//            } else {
//                response.put("lastUpdated", "N/A");
//            }
//
//            List<String> dates = new ArrayList<>();
//            List<Double> prices = new ArrayList<>();
//            for (HistoricalDataEntity historicalData : company.getHistoricalData()) {
//                dates.add(historicalData.getDate().toString());
//                prices.add(historicalData.getLastTransactionPrice());
//            }
//
//            response.put("dates", dates);
//            response.put("prices", prices);
//            response.put("status", "success");
//        } catch (Exception e) {
//            logger.error("Error fetching company data: {}", e.getMessage());
//            response.put("status", "error");
//            response.put("message", e.getMessage());
//        }
//        return response;
//    }
//
//    @GetMapping("/predict")
//    @ResponseBody
//    public Map<String, Object> getPrediction(@RequestParam(name = "companyId") Long companyId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            logger.info("Predicting price for company ID: {}", companyId);
//
//            CompanyEntity company = companyService.findById(companyId);
//            if (company == null) {
//                logger.warn("Company not found for prediction, ID: {}", companyId);
//                throw new Exception("Company not found for prediction, ID: " + companyId);
//            }
//
//            List<HistoricalDataEntity> historicalData = company.getHistoricalData();
//            logger.info("Historical data size for company ID {}: {}", companyId, historicalData.size());
//
//            List<Double> prices = historicalData.stream()
//                    .map(HistoricalDataEntity::getAveragePrice)
//                    .collect(Collectors.toList());
//            logger.info("Prepared data for prediction: size={}", prices.size());
//
//            Double predictedPrice = pricePredictionService.predictNextMonth(companyId);
//
//            if (predictedPrice == null) {
//                throw new Exception("Prediction failed or no result returned for company ID: " + companyId);
//            }
//
//            response.put("predictedPrice", predictedPrice);
//            response.put("status", "success");
//        } catch (Exception e) {
//            logger.error("Error predicting price for company ID {}: {}", companyId, e.getMessage());
//            response.put("status", "error");
//            response.put("message", "Prediction failed. " + e.getMessage());
//        }
//        return response;
//    }
//
////    @PostMapping("/runPythonScript")
////    @ResponseBody
////    public String runPythonScript(@RequestBody AnalysisRequest request) throws IOException, InterruptedException {
////        String analysisType = request.getAnalysisType();
////        String timeframe = request.getTimeframe();
////        String output = "";
////
////        if ("technical".equals(analysisType)) {
////            output = runTechnicalAnalysis(timeframe);
////        } else {
////            output = runFundametanlAnalysis();
////        }
////
////        return output;
////    }
////
////    public String runTechnicalAnalysis(String timeFrame) throws IOException, InterruptedException {
////        if ("1_day".equals(timeFrame)) {
////            timeFrame = "1";
////        } else if ("1_week".equals(timeFrame)) {
////            timeFrame = "7";
////        } else if ("1_month".equals(timeFrame)) {
////            timeFrame = "30";
////        }
////
////
////        ProcessBuilder processBuilder = new ProcessBuilder(
////                "C:\\Users\\Skipio\\AppData\\Local\\Programs\\Python\\Python312\\python.exe",
////                "scripts/sentiment_analysis.py",
////                saved_company,
////                timeFrame
////        );
////
////        processBuilder.redirectErrorStream(true);
////        Process process = processBuilder.start();
////
////        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////        StringBuilder output = new StringBuilder();
////        String line;
////        while ((line = reader.readLine()) != null) {
////            output.append(line).append("\n");
////        }
////
////        int exitCode = process.waitFor();
////
////        if (exitCode == 0) {
////            logger.info("Python script executed successfully.");
////        } else {
////            logger.error("Python script failed with exit code {}", exitCode);
////        }
////
////        return output.toString();
////    }
////    public String runFundametanlAnalysis() throws IOException, InterruptedException {
////        ProcessBuilder processBuilder = new ProcessBuilder(
////                "C:\\Users\\Skipio\\AppData\\Local\\Programs\\Python\\Python312\\python.exe",
////                    "C:\\Users\\Skipio\\Desktop\\ScipioDS SpotterMK main Homework3-Project\\scripts\\sentiment_analysis.py",
////                    saved_company.toLowerCase()
////        );
////
////        processBuilder.redirectErrorStream(true);
////        Process process = processBuilder.start();
////
////        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////        StringBuilder output = new StringBuilder();
////        String line;
////        while ((line = reader.readLine()) != null) {
////            output.append(line).append("\n");
////            //if(line.contains("NOT FOUND") || line.contains("BUY") || line.contains("SELL"))
////
////        }
////
////        int exitCode = process.waitFor();
////
////        if (exitCode == 0) {
////            logger.info("Python script executed successfully.");
////        } else {
////            logger.error("Python script failed with exit code {}", exitCode);
////        }
////
////        return output.toString();
////    }
////
////    public static class AnalysisRequest {
////        private String analysisType;
////        @JsonProperty("timeframe")
////        private String timeframe;
////
////        // Getters and setters
////        public String getAnalysisType() {
////            return analysisType;
////        }
////
////        public void setAnalysisType(String analysisType) {
////            this.analysisType = analysisType;
////        }
////
////        public String getTimeframe() {
////            return timeframe;
////        }
////
////        public void setTimeframe(String timeframe) {
////            this.timeframe = timeframe;
////        }
////    }
//}
