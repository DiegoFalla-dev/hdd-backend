package com.cineplus.cineplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ECommerceCineplusBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceCineplusBackendApplication.class, args);
	}

}
