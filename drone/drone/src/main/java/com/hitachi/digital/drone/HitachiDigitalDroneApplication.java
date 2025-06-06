package com.hitachi.digital.drone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HitachiDigitalDroneApplication {

	public static void main(String[] args) {
		SpringApplication.run(HitachiDigitalDroneApplication.class, args);
	}

}
