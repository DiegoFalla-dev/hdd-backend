
package com.cineplus.cineplus;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.cineplus.cineplus.domain.entity")
@EnableJpaRepositories(basePackages = "com.cineplus.cineplus.domain.repository")
public class ECommerceCineplusBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceCineplusBackendApplication.class, args);
	}

}
