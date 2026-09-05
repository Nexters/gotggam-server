package com.nexters.gotggam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@SpringBootApplication
public class GotggamApplication {

	public static void main(String[] args) {
		SpringApplication.run(GotggamApplication.class, args);
	}

}
