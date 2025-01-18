package com.example.project1.service;

import com.example.project1.entity.HistoricalDataEntity;

import java.util.List;

public interface DatabaseConnector {
    public List<String> findAllNames();
    public List<HistoricalDataEntity> getHistoricalData(String tableName);
}
