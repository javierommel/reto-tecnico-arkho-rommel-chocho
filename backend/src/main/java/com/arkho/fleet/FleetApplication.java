package com.arkho.fleet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FleetApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetApplication.class, args);
	}

}
