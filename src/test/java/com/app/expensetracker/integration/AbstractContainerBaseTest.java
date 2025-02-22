package com.app.expensetracker.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.util.function.Supplier;

public abstract class AbstractContainerBaseTest {

    static final MySQLContainer MY_SQL_CONTAINER;
    static final Supplier<Object> DATABASE_DRIVER = () -> "com.mysql.cj.jdbc.Driver";

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withDatabaseName("spring-boot-integration-test")
                .withUsername("root")
                .withPassword("root");
        System.out.println("Starting MySQL container...");
        MY_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void setDynamicPropertySource(DynamicPropertyRegistry registry) {

        //mysql
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", DATABASE_DRIVER);
    }
}
