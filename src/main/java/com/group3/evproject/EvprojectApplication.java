package com.group3.evproject;

import jdk.jfr.Enabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
public class EvprojectApplication {

	public static void main(String[] args) {
        SpringApplication.run(EvprojectApplication.class, args);
	}
}
