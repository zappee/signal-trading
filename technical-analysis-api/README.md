# API provides data for technical analysis

## 1) Overview
This API exports data to CVS file based on the given criterias.
The exported data can be used to generated different charts in Microsoft Excel.

## 2) Database configuration (PostgreSQL)
For more information please read the paragraph (2) in [coinbase pro trade history saver](../trade-history-saver) documentation.

## 3) Java Build
~~~~
$ PATH=/usr/java/jdk-11.0.4/bin:$PATH
$ cd signal-trading/technical-analysis-api
$ mvn clean package
~~~~

## 4) Run the application
The application needs to be configured properly before it is started. The configuration file sits in the `signal-trading/technical-analysis-api/src/main/resources` directory.


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
|logging.file.name=|not used|Application log to file.|

### 4.3) Run the application

~~~~
$ PATH=/usr/java/jdk-11.0.4/bin:$PATH
$ cd signal-trading/technical-analysis-api
$ mvn org.springframework.boot:spring-boot-maven-plugin:run
~~~~

## 5) REST Endpoints
### 5.1) Radar chart
* URL: `GET /api/radar`
* Parameters:
    * ticker: the name of the product
    * interval: requested interval back in time from now
    * scale: the granularity of the data
* Example request: `GET` [http://localhost:8081/api/radar?ticker=ETH-EUR&interval=300&scale=60](http://localhost:8081/api/radar?ticker=ETH-EUR&interval=300&scale=60)
* Responses: HTTP 400 or HTTP 200 with the generated CVS file

Example charts:
[daily radar chart with 1h scale](docs/example-chatrs/ETH-EUR%20daily%20radar%20chart.png)

[weekly radar chart shows trends in price and volume within days (from Monday to Sunday](docs/aaa.png)

[daily historical prices with 1h scale](docs/example-chatrs/ETH-EUR%20daily%20line%20chart.png)

