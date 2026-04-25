package com.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LinkedInModelApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedInModelApplication.class, args);
	}

}
