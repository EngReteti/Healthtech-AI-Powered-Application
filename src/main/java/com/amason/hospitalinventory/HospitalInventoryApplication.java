package com.amason.hospitalinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

// @EnableCaching turns on Spring's caching system app-wide - without 
// this, @Cacheable annotations elsewhere in the code would silently 
// do nothing at all
@SpringBootApplication
@EnableCaching
public class HospitalInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalInventoryApplication.class, args);
    }
}
