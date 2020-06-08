package com.hciot.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = "com.hciot.sample.client")
public class Start {

	public static void main(String[] args) {
		SpringApplication.run(Start.class);
	}

}
