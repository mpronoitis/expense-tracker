package com.app.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.app.expensetracker")
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        System.setProperty("testcontainers.docker.client.strategy", "org.testcontainers.dockerclient.UnixSocketClientProviderStrategy");
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }

}
