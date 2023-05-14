package com.example.courseworkbyzayats.config;

import com.example.courseworkbyzayats.services.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests((auth) ->  auth
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/styles/**").permitAll()
                        .requestMatchers("/scripts/**").permitAll()
                        .requestMatchers("/zayct/courses/signup/**").permitAll()
                        .requestMatchers("/zayct/stats/**").hasRole("ADMIN")
                        .requestMatchers("/zayct/import/**").hasRole("ADMIN")
                        .requestMatchers("/zayct/courses/list/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/zayct/courses/user/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .anyRequest().authenticated()
                )
                .formLogin().permitAll().defaultSuccessUrl("/zayct/courses/user", true)
                .and()
                .logout().permitAll()
                .and()
                .userDetailsService(jpaUserDetailsService)
                .headers(headers -> headers.frameOptions().sameOrigin())
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
