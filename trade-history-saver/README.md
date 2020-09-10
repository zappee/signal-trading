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
