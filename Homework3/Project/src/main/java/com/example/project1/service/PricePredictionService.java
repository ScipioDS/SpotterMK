package com.example.project1.service;

import com.example.project1.controller.CompanyController;
import com.example.project1.entity.HistoricalDataEntity;
import com.example.project1.repository.HistoricalDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricePredictionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final HistoricalDataRepository historicalDataRepository;
    private static final Logger logger = LoggerFactory.getLogger(PricePredictionService.class);

    private final String predictionApiUrl = "http://127.0.0.1:8000/predict-next-month-price/";

    public Double predictNextMonth(Long companyId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //List<HistoricalDataEntity> data = historicalDataRepository.findByCompanyIdAndDateBetween(companyId, LocalDate.now().minusMonths(3), LocalDate.now());
        // Fetch the last 30 entries
        Pageable pageable = PageRequest.of(0, 100); // Page 0, 30 entries
        List<HistoricalDataEntity> data = historicalDataRepository.findTop30ByCompanyIdOrderByDateDesc(companyId, pageable);

        Map<String, Object> requestBody = Map.of("data", mapToRequestData(data));
        logger.info("Prediction request payload: {}", requestBody);
        // Send the payload to the prediction service (use your existing API call)

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        Map<String, Double> response = restTemplate.postForObject(predictionApiUrl, requestEntity, Map.class);

        return response != null ? response.get("predicted_next_month_price") : null;
    }

    public static List<Map<String, Object>> mapToRequestData(List<HistoricalDataEntity> historicalDataEntities) {
        return historicalDataEntities.stream().map(entity -> {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("date", entity.getDate().toString());
            dataMap.put("average_price", entity.getAveragePrice());
            return dataMap;
        }).collect(Collectors.toList());
    }

}