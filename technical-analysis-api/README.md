# API provides data for technical analysis

## 1) Overview
This API exports data to CVS file based on the given criteria.
The exported data can be used to generate different kinds of charts in Microsoft Excel.

## 2) Database configuration (PostgreSQL)
For more information please read the paragraph (2) in [coinbase pro trade history saver](../trade-history-saver) documentation.

Either you do not have access to the CoinbasePro Crypto Exchange or you are not able to start the [coinbase pro trade history saver](../trade-history-saver) application in order to gather trading data, then you can use the saved database export file which contains almost two weeks trading data.
The export is a PostgreSQL database export file, you can load it with the following command: `psql -U <username> crypto < technical-analysis-api/docs/database-export/crypto-export.pgsql`.
## 3) Java Build
~~~~
$ PATH=/usr/java/jdk-12.0.2/bin:$PATH
$ cd signal-trading/technical-analysis-api
$ mvn clean package
~~~~

## 4) Run the application
The application needs to be configured properly before the first run. The application configuration file sits in the `signal-trading/technical-analysis-api/src/main/resources` directory.


### 4.1) Configuration parameters, must be set before the first run

|name|default value|description|
|---|---|---|
|server.port|8081|The port when the API serves the incoming requests.|
|datasource.driver-class-name|org.postgresql.Driver|JDBC driver class name.|
|datasource.url|jdbc:postgresql://localhost:5432/crypto|JDBC connection string.|
|datasource.username|NA|Name for the login.Name for the database login.|
|datasource.password|NA|Password for the connecting user.|

### 4.2) Advanced configuration parameters

|name|default value|description|
|---|---|---|
|logging.file.name|not applied|Path to the logfile.|

### 4.3) Run the application

~~~~
$ PATH=/usr/java/jdk-12.0.2/bin:$PATH
$ cd signal-trading/technical-analysis-api
$ mvn org.springframework.boot:spring-boot-maven-plugin:run
~~~~

## 5) REST Endpoints
### 5.1) API parameters
* ticker: Only tickers, supported by CoinbasePro is acceptable. You can get the list of the supported tickers with the [trade-history-saver](../trade-history-saver) application. Example tickers: BTC-EUR, ETH-EUR, XRP-USD.
* interval, scale: Value in seconds.

    |accepted|parameter value|
    |---|---|
    |1 minute|60|
    |5 minutes|300|
    |15 minutes|900|
    |30 minutes|1800|
    |1 hour|3600|
    |2 hours|7200|
    |4 hours|14400|
    |8 hours|28800|
    |1 day|86400|
    |1 week|604800|
    |1 month|2592000|
    |1 year|31622400|

* period start and end: timestamp in UTC timezone, format: `yyyy-mm-ddThh.mm.ss`

### 5.2) Historical average price and volume API
#### 5.2.1) With interval
* URL: `GET /api/fix-interval`
* Parameters:
    + ticker: the name of the product
    + interval: requested interval back in time from now
    + scale: the granularity of the data
* Example requests:
    + last hour with one-minute timeframe: `GET` [http://localhost:8081/api/fix-interval?ticker=ETH-EUR&interval=3600&scale=60](http://localhost:8081/api/fix-interval?ticker=ETH-EUR&interval=3600&scale=60)
    + last day with one-minute timeframe: `GET` [http://localhost:8081/api/fix-interval?ticker=ETH-EUR&interval=86400&scale=60](http://localhost:8081/api/fix-interval?ticker=ETH-EUR&interval=86400&scale=60)
    + last month with daily timeframe: `GET` [http://localhost:8081/api/fix-interval?ticker=ETH-EUR&interval=2592000&scale=86400](http://localhost:8081/api/fix-interval?ticker=ETH-EUR&interval=2592000&scale=86400)
* Responses: HTTP 400 or HTTP 200 with the generated CVS file

#### 5.2.2) With period start and end
* URL: `GET /api/period`
* Parameters:
    * ticker: the name of the product
    * start: start of the period
    * end: end of the period
    * scale: the granularity of the data
* Example requests:
    + daily chart with 30-minute timeframe: `GET` [http://localhost:8081/api/period?ticker=ETH-EUR&start=2020-09-14T00:00:00&end=2020-09-15T00:00:00&scale=86400](http://localhost:8081/api/period?ticker=ETH-EUR&start=2020-09-14T00:00:00&end=2020-09-15T00:00:00&scale=86400)
* Responses: HTTP 400 or HTTP 200 with the generated CVS file

#### 5.2.3) Weekly average price with 7 timelines for each day of the week API
* URL: `GET /api/weekly`
* Parameters:
    * ticker: the name of the product
    * day: any day in the week
    * scale: the granularity of the data
* Example requests:
    + weekly chart to show the pattern of changing in prices within days: `GET` [http://localhost:8081/api/weekly?ticker=ETH-EUR&day=2020-09-21&scale=1800](http://localhost:8081/api/weekly?ticker=ETH-EUR&day=2020-09-21&scale=1800)
* Responses: HTTP 400 or HTTP 200 with the generated CVS file

## 6) Example charts

* historical price and volume chart for the last hour with 60-seconds scale:

    ![](docs/example-chatrs/last-hour-price-and-volume.png)

* daily historical radar chart with 30-minutes scale:

    ![](docs/example-chatrs/daily-price.png)

* daily historical price and volume chart with 60-minutes scale:

    ![](docs/example-chatrs/daily-price-and-volume.png)

* weekly radar chart shows trends in price and volume within days (from Monday to Sunday):

    ![](docs/example-chatrs/weekly-price-pattern.png)
    
You can download the Excel spreadsheet from [here](docs/example-chatrs/charts.xlsx) which contains the data and the charts. 