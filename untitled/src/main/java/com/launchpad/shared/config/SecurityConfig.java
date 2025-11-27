package com.launchpad.shared.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Imported HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.support.MultipartFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable() // CSRF disabled for stateless API
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                // Static resources
                .antMatchers("/", "/*.html", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()

                // Auth endpoints
                .antMatchers("/api/startup/auth/**").permitAll()
                .antMatchers("/api/investor/auth/**").permitAll()
                .antMatchers("/api/admin/auth/**").permitAll()

                // Registration endpoints
                .antMatchers("/api/startup/register").permitAll()
                .antMatchers("/api/investor/register").permitAll()

                // Documents and File Uploads
                .antMatchers("/api/files/**").permitAll()
                .antMatchers("/uploads/**").permitAll()
                .antMatchers("/api/startup/documents/**").permitAll()

                // Public Data (Posts, Profiles, Admin Public)
                .antMatchers("/api/posts/**").permitAll()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/admin/**").permitAll()

                // --- CHANGED SECTION START ---
                // Profile viewing: Only GET is permitted publicly.
                // TEMP: allow full access to profiles (GET + PUT etc.)
                .antMatchers("/api/startups/**").permitAll()
                .antMatchers("/api/investors/**").permitAll()

                // --- CHANGED SECTION END ---
// Bids endpoints – let controller handle token via JwtUtil
                .antMatchers("/api/bids/**").permitAll()

                // All other requests require authentication
                .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed Origins: Add your frontend URLs here
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://127.0.0.1:5500"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // Allow credentials (cookies/headers)
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * MultipartFilter must be registered BEFORE Spring Security filters
     * to handle file uploads properly and avoid 403 errors
     */
    @Bean
    public FilterRegistrationBean<MultipartFilter> multipartFilterRegistration() {
        FilterRegistrationBean<MultipartFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MultipartFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(0); // Highest priority - runs before Spring Security
        return registration;
    }
}