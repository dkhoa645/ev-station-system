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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
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
//                        .requestMatchers("/member/**").hasAnyAuthority("ROLE_ADMIN","ROLE_MEMBER")
//                        .requestMatchers("/company/**").hasAnyAuthority("ROLE_ADMIN","ROLE_COMPANY")
//                        .requestMatchers("/driver/**").hasAnyAuthority("ROLE_ADMIN","ROLE_COMPANY","ROLE_DRIVER")
//                        .requestMatchers("/staff/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
//                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults()); // HTTP Basic mặc định
        http.oauth2ResourceServer(oauth2 ->
                oauth2.bearerTokenResolver(request -> {
                            String path = request.getServletPath();
                            if (path.contains("swagger") || path.contains("api-docs") || path.contains("webjars") || path.startsWith("/auth")) {
                                return null;
                            }
                            return new DefaultBearerTokenResolver().resolve(request);
                        })
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
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
