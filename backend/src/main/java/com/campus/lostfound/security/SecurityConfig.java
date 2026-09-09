package com.campus.lostfound.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter){this.jwtFilter=jwtFilter;}

    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
            .cors(cors->cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a
                .requestMatchers("/api/auth/**","/error").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/items","/api/items/*","/api/items/*/matches").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST,"/api/items").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.PUT,"/api/items/*").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.PATCH,"/api/items/*/recover").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE,"/api/items/*").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration c=new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173"));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**",c);
        return s;
    }
}
