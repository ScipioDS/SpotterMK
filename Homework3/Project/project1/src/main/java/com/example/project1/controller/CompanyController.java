package com.example.project1.controller;

import com.example.project1.entity.CompanyEntity;
import com.example.project1.entity.HistoricalDataEntity;
import com.example.project1.service.CompanyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    String saved_company="";

    @GetMapping("/home")
    public String getIndexPage(Model model) {
        logger.info("Rendering index page with company list.");
        List<CompanyEntity> companyEntityList= new ArrayList<>();
        companyEntityList.add(new CompanyEntity("adin"));
        model.addAttribute("companies", companyEntityList);
        return "select_and_predict";
    }

    @GetMapping("/company")
    @ResponseBody
    public Map<String, Object> getCompanyData(@RequestParam(name = "companyId") Long companyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching data for company ID: {}", companyId);
            CompanyEntity company = companyService.findById(companyId);
            String saved_company=company.getCompanyCode();
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
    @PostMapping("/runPythonScript")
    @ResponseBody
    public String runPythonScript() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:/Users/ncvet/AppData/Local/Programs/Python/Python313/python.exe",
                    "test.py",
                    saved_company
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();


            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }


            int exitCode = process.waitFor();


            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.out.println("Python script failed with exit code " + exitCode);
            }

            return output.toString();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error running the Python script: " + e.getMessage();
        }

    }
}