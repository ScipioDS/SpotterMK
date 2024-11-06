DAS Project @ FCSE 2024/25

Authors: Novica Cvetkoski, Aleksandra Krusharoska, David Stojkov

# SpotterMK

The "SpotterMK" app is a powerful tool designed for investors and analysts interested in the Macedonian Stock Exchange (MSE). The app automatically scrapes historical data from the MSE, going back ten years, to offer users an extensive overview of stock performance trends. By analyzing this data, the app uses machine learning algorithms to generate predictions on stock movement, helping users identify which stocks are likely to go up or down. The systems will store the date in structured files, nomially .csv files.

In addition to historical data, MSE Insight integrates news articles from the MSE website, analyzing relevant headlines and stories using Arteficial Intelligence. This news data is factored into the predictive models, recognizing the impact of events, economic reports, and other market-moving announcements on stock prices. By combining both quantitative data (historical stock performance) and qualitative data (news analysis), the app provides users with a more nuanced forecast of potential stock movements.

## Functional Requirements
### 1. Data Scraping and Collection
- The system can scrape historical stock prices from the Macedonian Stock Exchange, covering the last 10 years.
- The system must pull stock data to keep predictions current.
- The system can scrape relevant news stories from the MSE website, those that mention specific stocks, companies, or economic conditions affecting the market.
### 2. Data Processing and Storage
- The system can clean and format raw stock and news data to comply with existing data-structures.
- The system will be able to analyze news content to determine the sentiment (positive, neutral, negative) and evaluate the potential impact on stock performance.
### 3. User Interface
- The system will provide a dashboard displaying stock trends, predictions, and relevant news summaries.
- The system will allow users to search for specific stocks to view their historical performance and current trends.

## Non-functional Requirements
- The system will display stock data and predictions within 5 seconds of a user request.
- Historical and real-time data scraping, processing, and predictions should be completed within a daily interval to ensure near real-time accuracy.
- The system will be available 99% of the time, ensuring minimal downtime for users.
- The system must automatically retry scraping or data processing tasks in case of failures and log any persistent issues.
- The app should have an intuitive design and a straightforward navigation structure to allow easy access to predictions, stock data, and news analysis.
- The application should be modular to facilitate easy updates and maintenance of individual components, such as the scraping module, prediction engine, and user interface.

## User Scenarios
Daily Stock Monitoring by an Investor

Scenario: Ana, a retail investor, wants to monitor her portfolio and keep track of any potential price changes that could impact her investments.

Actions:
- Ana logs into MSE Insight and views the dashboard, where she sees an overview of her favorite stocks.
- She clicks on each stock to view predictions for price movements over the next day, week, and month, based on historical trends and recent news analysis.

Goal: Ana wants to make informed decisions about buying or selling stocks based on the 
