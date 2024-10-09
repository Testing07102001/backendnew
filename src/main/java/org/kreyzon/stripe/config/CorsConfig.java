package org.kreyzon.stripe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://impulz-lms.com");
        config.addAllowedOrigin("http://impulz-lms.com");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://jurong-academy.vercel.app/");
        config.addAllowedOrigin("https://869d-49-205-87-26.ngrok-free.app/");
//        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod(" GET, POST, PATCH, PUT, DELETE, OPTIONS");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
