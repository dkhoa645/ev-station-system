package com.group3.evproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvprojectApplication.class, args);

        System.out.println("SENDGRID_USERNAME=" + System.getenv("SENDGRID_USERNAME"));
        System.out.println("SENDGRID_API_KEY=" + System.getenv("SENDGRID_API_KEY"));
	}

}
