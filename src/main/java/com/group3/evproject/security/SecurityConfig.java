package com.group3.evproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // hoặc BCryptPasswordEncoder(10)
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // tắt CSRF cho API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/auth/**").permitAll() // tất cả POST /auth/** bỏ qua
                        .anyRequest().authenticated()                              // các endpoint khác yêu cầu auth
                )
                .httpBasic(withDefaults()); // HTTP Basic mặc định

        return http.build();
    }
}
