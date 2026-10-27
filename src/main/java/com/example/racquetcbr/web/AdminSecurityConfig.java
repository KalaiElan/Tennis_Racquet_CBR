package com.example.racquetcbr.web;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AdminSecurityConfig {
 @Bean
 @Order(1)
 SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
  return http.securityMatcher("/admin", "/admin/**")
      .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
      .httpBasic(Customizer.withDefaults()).build();
 }

 @Bean
 @Order(2)
 SecurityFilterChain publicSecurity(HttpSecurity http) throws Exception {
  return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .csrf(AbstractHttpConfigurer::disable).build();
 }

 @Bean
 UserDetailsService adminUsers(@Value("${ADMIN_USERNAME:admin}") String username,
      @Value("${ADMIN_PASSWORD:}") String password) {
  String secret = password.isBlank() ? UUID.randomUUID().toString() : password;
  String encoded = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(secret);
  return new InMemoryUserDetailsManager(User.withUsername(username).password(encoded).roles("ADMIN").build());
 }
}