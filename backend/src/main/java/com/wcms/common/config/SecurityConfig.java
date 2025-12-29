package com.wcms.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and() // Disable CORS restrictions based on WebMvc config
                .csrf().disable() // Disable CSRF for APIs
                .authorizeRequests()
                .antMatchers("/**").permitAll(); // Permit all, let Controllers handle logic

        return http.build();
    }
}
