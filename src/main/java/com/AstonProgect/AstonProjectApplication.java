package com.AstonProgect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.AstonProgect"})
public class AstonProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AstonProjectApplication.class, args);
	}
}
