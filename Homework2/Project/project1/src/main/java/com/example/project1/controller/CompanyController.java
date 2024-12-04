package com.example.project1.controller;

import com.example.project1.entity.CompanyEntity;
import com.example.project1.entity.HistoricalDataEntity;
import com.example.project1.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @GetMapping("/")
    public String getIndexPage(Model model) {
        logger.info("Rendering index page with company list.");
        model.addAttribute("companies", companyService.findAll());
        return "select_and_predict";
    }

    @GetMapping("/company")
    @ResponseBody
    public Map<String, Object> getCompanyData(@RequestParam(name = "companyId") Long companyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching data for company ID: {}", companyId);
            CompanyEntity company = companyService.findById(companyId);

            if (company == null) {
                logger.warn("Company not found for ID: {}", companyId);
                throw new Exception("Company not found for ID: " + companyId);
            }

            response.put("companyCode", company.getCompanyCode());

            // Handle lastUpdated
            if (company.getLastUpdated() != null) {
                response.put("lastUpdated", company.getLastUpdated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            } else {
                response.put("lastUpdated", "N/A");
            }

            // Add historical data
            List<String> dates = new ArrayList<>();
            List<Double> prices = new ArrayList<>();
            for (HistoricalDataEntity historicalData : company.getHistoricalData()) {
                dates.add(historicalData.getDate().toString());
                prices.add(historicalData.getLastTransactionPrice());
            }

            response.put("dates", dates);
            response.put("prices", prices);
            response.put("status", "success");
        } catch (Exception e) {
            logger.error("Error fetching company data: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    @GetMapping("/predict")
    @ResponseBody
    public Map<String, Object> getPrediction(@RequestParam(name = "companyId") Long companyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Predicting price for company ID: {}", companyId);

            CompanyEntity company = companyService.findById(companyId);
            if (company == null) {
                logger.warn("Company not found for prediction, ID: {}", companyId);
                throw new Exception("Company not found for prediction, ID: " + companyId);
            }

            // Simulate prediction logic (replace with actual logic)
            double predictedPrice = company.getHistoricalData()
                    .stream()
                    .mapToDouble(HistoricalDataEntity::getLastTransactionPrice)
                    .average()
                    .orElse(0.0);

            response.put("predictedPrice", predictedPrice);
            response.put("status", "success");
        } catch (Exception e) {
            logger.error("Error predicting price: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "Not enough data for prediction.");
        }
        return response;
    }
}
