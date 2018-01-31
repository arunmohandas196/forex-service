# Forex-Service project

This is a Java / Maven / Spring Boot (version 1.5.6) application that can be used to retrieve currency rates for target currencies based on a source currency for an exchange date.


## How to Run 

This application is packaged as a jar which has Tomcat 8 embedded.

* Clone this repository .
* Make sure you are using JDK 1.8 and Maven 3.x.
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:
```
        java -jar -Dspring.profiles.active=test target/forex-service-0.1.0.jar
or
        mvn spring-boot:run -Drun.arguments="spring.profiles.active=test"
```
* Check the stdout or boot_forex_service.log file to make sure no exceptions are thrown

Once the application runs you should see something like this

```
2017-09-05 01:22:37.927  INFO 69056 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8090 (http)
2017-09-05 01:22:37.932  INFO 69056 --- [           main] com.amohandas.forex.Application          : Started Application in 6.752 seconds (JVM running for 7.093)
```

## About the Service

The service is a Forex Currency exchange service, which provides currency rates for target currencies based on a source currency for an exchange date.
When a call is made for retrieval of exchange rates, it retrieves forex details from 3rd party forexApi and populate them in the server db.
For subsequent calls for the same criteria, data gets picked from the server db instead of making the 3rd party api call till the record hasn't expired.
There is a configuration property "forex.service.expiryDateForCurrencyInSeconds" in application.yml file, after which created data in db gets expired and call gets made to 3rd party api.
Expiry logic is only applicable for today's forex data. For today when multiple calls are made, it maintains only one record in db which will be re-inserted with latest upon expiration.
For historical dates' forex data, data gets picked from db if it's already present, else 3rd party call is made to retrieve this data populated to db and it never expires.

## DB
Currently it uses an in-memory database (H2) to store the data for ease testing and installation. This is already setup with the current config and no need of installation is required.
You can also do with a relational database like MySQL or PostgreSQL with ease, steps mentioned below.

## 3rd Party API for Forex
Currently it uses http://fixer.io/ api for retrieving forex details.
The rates are updated daily around 4PM CET.
Most probably on testing current date data won't be available.
So it's preferred to lookup date latest of yesterday.
Integration with other 3rd party API is easy, and can be achieved by writing a client similiar to com.amohandas.forex.client.fixer.FixerClient and injecting that to ForexService

## Currently supported features:

* Full integration with the latest **Spring** Framework: inversion of control, dependency injection, etc.
* Packaging as a single jar with embedded container (tomcat 8): No need to install a container separately on the host just run using the ``java -jar`` command
* Currently supports spring pre-defined to set up healthcheck, metrics, info, environment, etc. endpoints automatically on a configured management port.
* RESTful service using annotation: supports JSON request / response
* Exception mapping from application exceptions to the right HTTP response with exception details in the body
* *Spring Data* Integration with JPA/Hibernate.
* Automatic CRUD functionality against the data source using Spring *Repository* pattern
* Demonstrates MockMVC test framework with associated libraries
* APIs are "self-documented" by Swagger2 using annotations
* More Integration test on endpoints needs to be added in com.amohandas.forex.test.ForexControllerTest
* Test coverage for this is in progress. Currently some of them are there in com.amohandas.forex.test.service.ForexServiceTest
* More logging and adding metrics are pending

Here are some endpoints we can call:

### Get information about system health, configurations, etc.

```
http://localhost:8091/env
http://localhost:8091/health
http://localhost:8091/info
http://localhost:8091/metrics
```

### Refer swagger url below to see currently supported Apis.

```
Run the server and browse to http://localhost:8090/swagger-ui.html

Sample API calls:
  GET  http://localhost:8090/forex/v1/latest?sourceCurrency=EUR&targetCurrencies=USD
  GET  http://localhost:8090/forex/v1/latest?sourceCurrency=EUR&targetCurrencies=USD,INR
  GET  http://localhost:8090/forex/v1/2017-09-04?sourceCurrency=EUR&targetCurrencies=USD
```

### To view your H2 in-memory datbase

The 'test' profile runs on H2 in-memory database. To view and query the database you can browse to http://localhost:8090/h2-console. Default username is 'sa' with a blank password. Make sure you disable this in your production profiles.

# Running the project with MySQL

This project uses an in-memory database so that we don't have to install a database in order to run it.
However, converting it to run with another relational database such as MySQL or PostgreSQL is very easy since the project uses Spring Data and the Repository pattern.

Here is what we would do to back the services with MySQL, for example:

### In pom.xml add: 

```
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```

### Append this to the end of application.yml: 

```
---
spring:
  profiles: mysql

  datasource:
    driverClassName: com.mysql.jdbc.Driver
    url: jdbc:mysql://<your_mysql_host_or_ip>/bootexample
    username: <your_mysql_username>
    password: <your_mysql_password>

  jpa:
    hibernate:
      dialect: org.hibernate.dialect.MySQLInnoDBDialect
      ddl-auto: update # todo: in non-dev environments, comment this out:


hotel.service:
  name: 'test profile:'
```

### Then run is using the 'mysql' profile:

```
        java -jar -Dspring.profiles.active=mysql target/spring-boot-rest-example-0.4.0.war
or
        mvn spring-boot:run -Drun.arguments="spring.profiles.active=mysql"
```

# Attaching to the app remotely from your IDE

Run the service with these command line options:

```
mvn spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
or
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dspring.profiles.active=test -Ddebug -jar target/forex-service-0.1.0.jar
```
and then you can connect to it remotely using your IDE. For example, from IntelliJ You have to add remote debug configuration: Edit configuration -> Remote.
