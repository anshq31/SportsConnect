package com.ansh.sportsconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportsConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsConnectApplication.class, args);
	}

}
