package com.example.external_billing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients 
public class ExternalBillingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalBillingApplication.class, args);
    }
}