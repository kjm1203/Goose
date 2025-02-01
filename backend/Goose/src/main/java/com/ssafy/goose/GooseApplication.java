package com.ssafy.goose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 🔹 @Scheduled 활성화
public class GooseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GooseApplication.class, args);
	}

}
