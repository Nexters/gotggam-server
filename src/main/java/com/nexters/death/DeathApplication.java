package com.nexters.death;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DeathApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeathApplication.class, args);
	}

}
