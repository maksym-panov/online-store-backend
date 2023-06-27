package com.panov.store.configuration;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * Spring MVC and JPA/Hibernate configuration class.
 *
 * @author Maksym Panov
 * @version 2.0
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.panov.store" })
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final String UNIT_NAME = "com.panov.store";

    /**
     * Configuration of origins permissions
     * @param registry - CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:3000");
    }

    /**
     * Provides an EntityManager factory
     *
     * @return EntityManagerFactory instance.
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory(UNIT_NAME);
    }
}
