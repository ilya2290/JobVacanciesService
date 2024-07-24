/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Work Agency API application.
 * <p>
 * This class is responsible for starting the Spring Boot application.
 * It also enables scheduling capabilities by using the @EnableScheduling annotation.
 */
@EnableScheduling
@SpringBootApplication
public class WorkAgencyAPIApplication {

	/**
	 * The main method that serves as the entry point for the application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(WorkAgencyAPIApplication.class, args);
	}

}
