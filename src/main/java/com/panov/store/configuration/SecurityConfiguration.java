package com.panov.store.configuration;

import com.panov.store.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration extends AbstractSecurityWebApplicationInitializer {
    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder encoder
    ) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic();

        http.logout()
                .logoutSuccessUrl("/products")
                .invalidateHttpSession(true);

        http.csrf().disable();

        http.authorizeHttpRequests().anyRequest().permitAll();

        http.formLogin();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
