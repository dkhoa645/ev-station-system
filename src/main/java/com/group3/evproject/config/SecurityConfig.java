package com.group3.evproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/auth"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()
                        .requestMatchers("/member/**").hasAnyRole("ADMIN","MEMBER")
                        .requestMatchers("/company/**").hasAnyRole("ADMIN","COMPANY")
                        .requestMatchers("/driver/**").hasAnyRole("ADMIN","COMPANY","DRIVER")
                        .requestMatchers("/staff/**").hasAnyRole("ADMIN","STAFF")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()); // HTTP Basic mặc định
        http.oauth2ResourceServer(oauth2 ->
                oauth2.bearerTokenResolver(request -> {
                            String path = request.getServletPath();
                            if (path.startsWith("/auth") ||
                                    path.startsWith("/swagger-ui") ||
                                    path.startsWith("/v3/api-docs") ||
                                    path.startsWith("/swagger-resources") ||
                                    path.startsWith("/webjars")) {
                                return null; // ✅ Bỏ qua JWT check
                            }
                            return new DefaultBearerTokenResolver().resolve(request);
                        })
                        .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
                );

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKey = new SecretKeySpec(signerKey.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

}
