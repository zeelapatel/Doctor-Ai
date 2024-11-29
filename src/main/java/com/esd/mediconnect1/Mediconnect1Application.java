package com.esd.mediconnect1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.esd.mediconnect1.dao")
@EntityScan(basePackages = "com.esd.mediconnect1.model")
public class Mediconnect1Application {

	public static void main(String[] args) {
		SpringApplication.run(Mediconnect1Application.class, args);
	}

}
