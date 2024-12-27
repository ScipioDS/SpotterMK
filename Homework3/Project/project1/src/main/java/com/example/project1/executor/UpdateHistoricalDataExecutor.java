//package com.example.project1.executor;
//
//import com.example.project1.Project1Application;
//import com.example.project1.data.pipeline.impl.DataAvailabilityCheckFilter;
//import com.example.project1.entity.CompanyEntity;
//import com.example.project1.repository.CompanyRepository;
//import com.example.project1.repository.HistoricalDataRepository;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public class UpdateHistoricalDataExecutor {
//
//    public static void main(String[] args) {
//        // Initialize Spring Application Context using the main application class
//        ApplicationContext context = SpringApplication.run(Project1Application.class, args);
//
//        // Get the required beans
//        CompanyRepository companyRepository = context.getBean(CompanyRepository.class);
//        HistoricalDataRepository historicalDataRepository = context.getBean(HistoricalDataRepository.class);
//
//        // Create the DataAvailabilityCheckFilter instance
//        DataAvailabilityCheckFilter filter = new DataAvailabilityCheckFilter(companyRepository, historicalDataRepository);
//
//        try {
//            // Fetch all existing companies
//            List<CompanyEntity> companies = companyRepository.findAll();
//
//            // Update historical data for the last month for each company
//            for (CompanyEntity company : companies) {
//                LocalDate fromDate = LocalDate.now().minusMonths(1); // Start of the last month
//                LocalDate toDate = LocalDate.now(); // Current date
//
//                // Fetch and add historical data
//                filter.addHistoricalData(company, fromDate, toDate);
//                System.out.println("Updated data for company: " + company.getCompanyCode());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error while updating historical data.");
//        }
//    }
//}
