package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // ⚠️ With credentials=true, you MUST NOT use "*". List explicit origins.
        cfg.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "http://localhost:4400",
            "http://localhost:4500",
            "http://localhost:4600"
            // add your production frontend origin: "https://app.your-domain.com"
        ));

        cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        // We’re using cookie auth; no need to expose Authorization
        // cfg.setExposedHeaders(List.of("Authorization"));

        // If your Angular app calls non-/api paths, register /** here.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
