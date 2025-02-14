# **💰 Expense Tracker - Spring Boot 3**

A RESTful API built using **Spring Boot 3**, **MySQL**, and **JWT Authentication**. This API allows users to track their expenses, set budgets, and manage financial records efficiently.

---

## **📚 Table of Contents**

- [Prerequisites](#prerequisites)
- [Database & Migrations (MySQL + Liquibase)](#database--migrations-mysql--liquibase)
- [Start the Application](#start-the-application)
- [Authorization with Spring AOP](#authorization-with-spring-aop)
- [Testing](#testing)
  - [Unit Testing](#unit-testing)

---

## **📋 Prerequisites**

Ensure you have the following installed on your system:

- **Java 23** → [Download Java 23](https://adoptium.net/)
- **MySQL** (used as the main database)
- **Maven** → [Install Maven](https://maven.apache.org/download.cgi)
- **Liquibase** (for managing database migrations)

---

## **🛢️ Database & Migrations (MySQL + Liquibase)**

This project uses **MySQL** as the primary database and **Liquibase** for managing schema migrations. Configure `application.yml` as follows:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expense_tracker
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
  liquibase:
    change-log: classpath:/db/changelog/db.changelog-master.xml
```

### **Run MySQL Database as a Docker Container**

If you don't have MySQL installed locally, you can start a MySQL container using Docker:

```sh
docker run --name mysql-expense-tracker -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=expense_tracker -p 3306:3306 -d mysql:latest
```

### **Apply Database Migrations**

Liquibase will automatically apply migrations when the application starts. However, you can also run migrations manually:

```sh
./mvnw liquibase:update
```

---

## **🚀 Start the Application**

Run the application using Maven:

```sh
./mvnw spring-boot:run
```

---

## **🔐 Authorization with Spring AOP**

This project uses **Spring AOP** to ensure users can only access resources that belong to them. Before accessing an expense, the system checks whether the logged-in user owns the expense.

### **Example of AOP Authorization Check**

```java
 @Around("executeSecurityLayer(securityLayer)")
    public Object beforeExecute(ProceedingJoinPoint pjp, SecurityLayer securityLayer) throws Throwable {
        Long idToCheck =  makeSecurityOwnerControl(pjp, securityLayer);
        if (idToCheck == null) {
            return pjp.proceed();
        }
        //check if the authenticated user has the same id with the requested IdToCheck;
        Long idLoggedUser = getLoggedUser();
        List<Long> queryUserIds =  createQuery(securityLayer, idToCheck);
        if (!queryUserIds.contains(idLoggedUser)) {
            throw new GenericBadRequestException("Security Control Error: You try to get a resource that does not belongs to you", ErrorType.IM_SECURITY_CONTROL_ERROR);
        }
        return pjp.proceed();
    }
```

Spring AOP ensures that only authorized users can perform actions on their own data, improving security and maintainability.

---
---

## **🧪 Testing**

### **Unit Testing**

Run unit tests:

```sh
./mvnw test
```


