package com.panov.store.configuration;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.panov.store" })
public class WebConfiguration implements WebMvcConfigurer {
    private final ApplicationContext context;
    private final String UNIT_NAME = "com.panov.store";

    @Autowired
    public WebConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory(UNIT_NAME);
    }
}
