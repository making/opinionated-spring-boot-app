package com.example.opinionated;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestOpinionatedSpringBootAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(OpinionatedSpringBootAppApplication::main)
			.with(TestOpinionatedSpringBootAppApplication.class)
			.run(args);
	}

}
