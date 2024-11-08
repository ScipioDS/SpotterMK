# DAS Project @ FSCE 2024/25
**SpotterMK - Macedonian Stock Exchange Analysis Project**

## Project Overview
SpotterMK is a project developed to analyze historical stock data from the Macedonian Stock Exchange. The project leverages the Pipe and Filter architecture to automate the extraction, transformation, and loading (ETL) of daily stock data for comprehensive analysis. The system processes at least the last 10 years of stock data on a daily basis while ensuring proper formatting for further analysis.

## Project Objectives
- **Implement a modular Pipe and Filter architecture for efficient data processing.**
- **Automate the download and processing of historical stock data for all listed issuers.**
- **Ensure the data is accurately transformed and stored in a format suitable for further analysis.**

## Functional Requirements
1. Automate the download of daily stock data from the Macedonian Stock Exchange for the past 10 years.
2. Filter and structure the extracted data to retain key information such as date, last transaction price, maximum price, minimum price, average price, percentage change, quantity, trading volume in BEST (denars), and total trading volume (denars).
3. Store processed data in CSV files or a database for future use.
4. Implement basic error handling for scenarios involving missing data formats.
5. (Planned) Design and implement a user interface to facilitate and monitor data processing tasks as part of the upcoming project phases, contributing to the development of a comprehensive web application integrated with the stock exchange. 

## Non-functional Requirements
1. The processing pipeline should handle large datasets and complete tasks efficiently.
2. The architecture should support future expansion to accommodate additional data sources or analysis features.
3. Ensure data integrity throughout the ETL process.
4. Modular code structure; current focus is on creating clean, maintainable code for easy future updates.
5. (Planned) A simple and intuitive user interface for ease of use.

## User Scenarios
### Scenario 1: Data Enthusiast Downloading Data
**Persona**: Alex, an individual interested in analyzing stock trends and historical data.
- **Goal**: Alex wants to download clean, formatted stock data for personal analysis and research.
- **Outcome**: Alex accesses the processed data, ensuring that it covers the last 10 years and is in a format suitable for importing into data analysis tools.
### Scenario 2: Technical Student
**Persona**: Marko, a computer science student studying data architectures.
- **Goal**: Marko examines the project to understand the Pipe and Filter architecture.
- **Outcome**: He gains insights from the modular design and processing flow demonstrated in the project.
