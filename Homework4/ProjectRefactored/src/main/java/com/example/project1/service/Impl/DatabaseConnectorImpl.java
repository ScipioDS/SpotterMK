package com.example.project1.service.Impl;

import com.example.project1.entity.HistoricalDataEntity;
import com.example.project1.service.DatabaseConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseConnectorImpl implements DatabaseConnector {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public static DatabaseConnectorImpl instance;

    private DatabaseConnectorImpl() {}
    public static synchronized DatabaseConnectorImpl getInstance()
    {
        if (instance == null)
            instance = new DatabaseConnectorImpl();

        return instance;
    }
    @Override
    public List<String> findAllNames(){
        String sql = "SELECT pg.tablename FROM pg_tables AS pg WHERE pg.schemaname = 'public' AND pg.tablename NOT IN ('company_entity','companies','codes_2','pg_tables','historical_data','code','codes_1')";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    @Override
    public List<HistoricalDataEntity> getHistoricalData(String tableName) {
        String sql = "SELECT * FROM " + tableName; // Dynamically append the table name

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<HistoricalDataEntity> dataEntities = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

        for (Map<String, Object> row : rows) {
            HistoricalDataEntity entity = new HistoricalDataEntity();

            entity.setDate(LocalDate.parse(String.valueOf(row.get("date")), formatter));
            entity.setLastTransactionPrice(Double.valueOf(String.valueOf(row.get("lasttransaction")).replace(".","").replace(",",".")));
            entity.setMaxPrice(Double.valueOf(String.valueOf(row.get("max")).replace(".","").replace(",",".")));
            entity.setMinPrice(Double.valueOf(String.valueOf(row.get("min")).replace(".","").replace(",",".")));
            entity.setAveragePrice(Double.valueOf(String.valueOf(row.get("avg")).replace(".","").replace(",",".")));
            entity.setPercentageChange(Double.valueOf(String.valueOf(row.get("prom")).replace(".","").replace(",",".")));
            entity.setQuantity(Integer.valueOf(String.valueOf(row.get("amount")).replace(".","").replace(",",".")));
            entity.setTurnoverBest(Integer.valueOf(String.valueOf(row.get("best")).replace(".","").replace(",",".")));
            entity.setTotalTurnover(Integer.valueOf(String.valueOf(row.get("total")).replace(".","").replace(",",".")));

            dataEntities.add(entity);
        }

        return dataEntities;

    }
}
