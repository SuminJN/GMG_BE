package com.gmg.jeukhaeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class JeukhaengApplication {

	public static void main(String[] args) {
		SpringApplication.run(JeukhaengApplication.class, args);
	}

}
