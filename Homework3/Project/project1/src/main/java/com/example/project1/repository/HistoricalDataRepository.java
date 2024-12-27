package com.example.project1.repository;

import com.example.project1.entity.CompanyEntity;
import com.example.project1.entity.HistoricalDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalDataEntity, Long> {
    Optional<HistoricalDataEntity> findByDateAndCompany(LocalDate date, CompanyEntity company);
    List<HistoricalDataEntity> findByCompanyIdAndDateBetween(Long companyId, LocalDate from, LocalDate to);
    @Query("SELECT h FROM HistoricalDataEntity h WHERE h.company.id = :companyId ORDER BY h.date DESC")
    List<HistoricalDataEntity> findTop30ByCompanyIdOrderByDateDesc(Long companyId, Pageable pageable);
}
