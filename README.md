# Loan Management System

This is a Loan Management System built using Spring Boot, Java 11, and H2 Database. The system allows for managing customer loans, installment payments, and applying interest rates based on specific business logic.
## How to Run

This application is packaged as a war which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the ```java -jar``` command.

* Clone this repository
* Make sure you are using JDK 11 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* java –jar <credit-0.0.1-SNAPSHOT.jar>.jar in target file

```
## About the Service

MyCommandLineRunner class generates the necessary data. When the application starts, an admin user and a customer user are inserted into the database.

To use the API, you can import the inghubs.postman_collection.json file into Postman. This will give you access to the following endpoints for credit service baseUrl:

* POST /api/user/login -login service with jwt token(In postman collection you can use admin-login for admin role and customer-login for user role.
* POST /api/loans/create - create loan service for customers
* GET /api/loans/list - list loans for customer
* GET /api/installments/{loanId} - get a specific loan
* GET /api/installments/pay - payment service for specific loan



```
#**App properties**
* spring.application.name=credit
*  
* spring.datasource.url=jdbc:h2:mem:testdb
* spring.datasource.driverClassName=org.h2.Driver
* spring.datasource.username=sa
* spring.datasource.password=password
* spring.h2.console.enabled=true
* spring.jpa.hibernate.ddl-auto=update
* server.tomcat.accesslog.enabled=true
* spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
* spring.main.allow-circular-references: true
* spring.jpa.hibernate.show-sql=true
*  
* rest.paging.default.page=0
* rest.paging.default.pageSize=50
* rest.paging.default.sortProperty=id
* rest.paging.default.sortDirection=desc

#**Technologies Used**

This project was built using the following technologies:


* Java 11
* Spring Boot
* Spring Data JPA
* Hibernate
* H2DB


# Questions: aanil1201@gmail.com



