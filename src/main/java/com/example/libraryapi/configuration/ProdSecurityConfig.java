package com.example.libraryapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class ProdSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(Customizer.withDefaults());

        httpSecurity
                .csrf(csrfConfigurer -> csrfConfigurer.disable());

        httpSecurity
                .authorizeHttpRequests(authorizationMatcher ->
                        authorizationMatcher
                                .requestMatchers(HttpMethod.POST, "/authors")
                                .hasRole("ADMIN")
/*
                                .requestMatchers()
                                .hasRole()
                                .requestMatchers()
                                .hasRole()
                                .
                                .
                                .
*/
                                .anyRequest()
                                .authenticated()
                );
        return httpSecurity.build();
    }
}