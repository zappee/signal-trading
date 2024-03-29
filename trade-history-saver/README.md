# CoinbasePro Exchange Trade History Saver

## 1) Overview
This application downloads trade data from CoinbasePro Crypto Exchange and store them in a database.
Each ticker has an own database table that means trade histories are saved separately per tickers.

>A ticker symbol or stock symbol is an abbreviation used to uniquely identify publicly traded shares of a particular stock on a particular stock market. A stock symbol may consist of letters, numbers or a combination of both.

## 2) Database configuration (PostgreSQL)
1. create a new user: `CREATE USER <user> WITH ENCRYPTED PASSWORD '<password>';`
1. create a new database: `CREATE DATABASE crypto OWNER somoarn ENCODING 'UTF8';`

## 3) Java Build
~~~~
$ PATH=/usr/java/jdk-12.0.2/bin:$PATH
$ cd signal-trading/trade-history-saver
$ mvn clean package
~~~~

## 4) Run the application
The application needs to be configured properly before the first run. The application configuration file sits in the `signal-trading/trade-history-saver/src/main/resources` directory.

### 4.1) Configuration parameters, must be set before the first run

|name|default value|description|
|---|---|---|
|exchange.coinbase.key|n/a|The Key will be randomly generated and provided by CoinbasePro.|
|exchange.coinbase.secret|n/a|The Secret will be randomly generated and provided by CoinbasePro.|
|exchange.coinbase.passphrase|n/a|Passphrase will be provided by you to further secure your API access. CoinbasePro stores the salted hash of your passphrase for verification, but cannot recover the passphrase if you forget it.|
|exchange.coinbase.tickers|ETH-EUR,ETH-USD,BTC-EUR,BTC-USD,XRP-EUR,XRP-USD|Products to download|
|datasource.driver-class-name|org.postgresql.Driver|JDBC driver class name.|
|datasource.url|jdbc:postgresql://localhost:5432/crypto|JDBC connection string.|
|datasource.username|NA|Name for the database login.|
|datasource.password|NA|Password for the connecting user.|
|exchange.coinbase.tickers|ETH-EUR,BTC-EUR,XRP-EUR,ETH-USD|Tickers to download.|

### 4.2) Advanced configuration parameters

|name|default value|description|
|---|---|---|
|exchange.coinbase.allowed-requests-within-period|2|CoinbasePro API endpoint has a custom rate limit by profile ID: 2 requests per second.|
|exchange.coinbase.period-length|1000|CoinbasePro API endpoint has a custom rate limit by profile ID: 2 requests per second.|
|exchange.coinbase.scheduler-delay|3000|The individual ticker downloaders are managed by a scheduler. They run parallel based on this configuration.|
|logging.file.name|not applied|Path to the logfile.|

### 4.3) Run the application

~~~~
$ PATH=/usr/java/jdk-12.0.2/bin:$PATH
$ cd signal-trading/trade-history-saver
$ mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments=<profile>
~~~~

Supported profiles:
* candles
* accounts
* products

## 5) Appendix

### 5.1) Export database schema
`pg_dump -U <username> crypto > crypto-export.pgsql`
