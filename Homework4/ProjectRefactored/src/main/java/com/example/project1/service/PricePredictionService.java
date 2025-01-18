//package com.example.project1.service;
//
//import com.example.project1.entity.HistoricalDataEntity;
//import com.example.project1.repository.HistoricalDataRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PricePredictionService {
//    private final CompanyService companyService;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final HistoricalDataRepository historicalDataRepository;
//    private static final Logger logger = LoggerFactory.getLogger(PricePredictionService.class);
//
//    private final String predictionApiUrl = "http://127.0.0.1:8000/predict-next-month-price/";
//
//    public Double predictNextMonth(Long companyId) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        List<HistoricalDataEntity> data = historicalDataRepository.findByCompanyIdAndDateBetween(companyId, LocalDate.now().minusYears(1), LocalDate.now());
//        if (data.size() >= 150) {
//            data = data.stream()
//                    .sorted(Comparator.comparing(HistoricalDataEntity::getDate).reversed())
//                    .limit(150)
//                    .collect(Collectors.toList());
//        }
//
////      Pageable pageable = PageRequest.of(0, 100);     // Fetch the last entries
////      List<HistoricalDataEntity> data = historicalDataRepository.findHistoricalDataByCompanyId(companyId, pageable);
//
//        Map<String, Object> requestBody = Map.of("data", mapToRequestData(data));
//        logger.info("Prediction request payload: {}", requestBody);
//        logger.info("Request Body: {}", requestBody);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//        Map<String, Double> response = restTemplate.postForObject(predictionApiUrl, requestEntity, Map.class);
//
//        return response != null ? response.get("predicted_next_month_price") : null;
//    }
//
//    public Double predictNextMonthDavid(String companyId) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        List<HistoricalDataEntity> data = new ArrayList<>();
//        for (HistoricalDataEntity historicalDataEntity : companyService.companyTest(companyId)) {
//            data.add(historicalDataEntity);
//        }
//
////      Pageable pageable = PageRequest.of(0, 100);     // Fetch the last entries
////      List<HistoricalDataEntity> data = historicalDataRepository.findHistoricalDataByCompanyId(companyId, pageable);
//
//        Map<String, Object> requestBody = Map.of("data", mapToRequestData(data));
//        logger.info("Prediction request payload: {}", requestBody);
//        logger.info("Request Body: {}", requestBody);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//        Map<String, Double> response = restTemplate.postForObject(predictionApiUrl, requestEntity, Map.class);
//
//        return response != null  response.get("predicted_next_month_price") : null;
//    }
//
//    public static List<Map<String, Object>> mapToRequestData(List<HistoricalDataEntity> historicalDataEntities) {
//        return historicalDataEntities.stream().map(entity -> {
//            Map<String, Object> dataMap = new HashMap<>();
//            dataMap.put("date", entity.getDate().toString());
//            dataMap.put("average_price", entity.getAveragePrice());
//            return dataMap;
//        }).collect(Collectors.toList());
//    }
//
//}
