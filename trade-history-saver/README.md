# Coinbase Exchange Trade History Saver

## 1) Overview
## 5) Run the application

~~~~
PATH=/usr/java/jdk-11.0.4/bin:$PATH
mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments=<profile>
~~~~

Supported profiles:
* candles
* accounts
* products

## 6) Initialize the database
1. create a new user: `CREATE USER <user> WITH ENCRYPTED PASSWORD '<password>';`
1. create a new database: `CREATE DATABASE crypto OWNER somoarn ENCODING 'UTF8';`
1. create the tables:
    ~~~~
    CREATE TABLE coinbase_eth_eur (
        trade_date TIMESTAMP PRIMARY KEY,
        lowest_price DECIMAL(11, 6) NOT NULL,
        highest_price DECIMAL(11, 6) NOT NULL,    
        opening_price DECIMAL(11, 6) NOT NULL,
        closing_price DECIMAL(11, 6) NOT NULL,
        volume DECIMAL(14,8) NOT NULL
    );
    
    CREATE TABLE coinbase_btc_eur (
        trade_date TIMESTAMP PRIMARY KEY,
        lowest_price DECIMAL(11, 6) NOT NULL,
        highest_price DECIMAL(11, 6) NOT NULL,    
        opening_price DECIMAL(11, 6) NOT NULL,
        closing_price DECIMAL(11, 6) NOT NULL,
        volume DECIMAL(14,8) NOT NULL
    );
    
    CREATE TABLE coinbase_xrp_eur (
        trade_date TIMESTAMP PRIMARY KEY,
        lowest_price DECIMAL(11, 6) NOT NULL,
        highest_price DECIMAL(11, 6) NOT NULL,    
        opening_price DECIMAL(11, 6) NOT NULL,
        closing_price DECIMAL(11, 6) NOT NULL,
        volume DECIMAL(14,8) NOT NULL
    );
    ~~~~
