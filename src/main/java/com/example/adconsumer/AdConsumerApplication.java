package com.example.adconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AdConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdConsumerApplication.class, args);
	}

}
