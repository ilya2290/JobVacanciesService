/**
 * <==================================>
 * Copyright (c) 2024 Ilya Sukhina.*
 * <=================================>
 */

package com.example.workaagencyapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for defining beans used in the application.
 */
@Configuration
public class AppConfig {

    /**
     * Provides a {@link RestTemplate} bean.
     * <p>
     * RestTemplate is used for making HTTP requests and interacting with RESTful web services.
     *
     * @return a new instance of RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Provides an {@link ObjectMapper} bean.
     * <p>
     * ObjectMapper is used for serializing and deserializing JSON data.
     *
     * @return a new instance of ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
