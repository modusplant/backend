package kr.modusplant.global.middleware.security;

import kr.modusplant.global.advice.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.debug.enabled}")
    private Boolean debugEnabled;

    private final AuthenticationConfiguration authenticationConfiguration;
    private final GlobalExceptionHandler globalExceptionHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(debugEnabled);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/*")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/terms").permitAll()
                        .requestMatchers("/api/members/*").permitAll()
                        .requestMatchers("/*/social-login").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers("/auth/token/refresh").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(eh ->
                        eh.authenticationEntryPoint((request, response, authException) ->
                                globalExceptionHandler.handleGenericException(request, authException))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                globalExceptionHandler.handleGenericException(request, accessDeniedException))
                )
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(300)) // 5분
                        .contentTypeOptions(Customizer.withDefaults())
                );
        return http.build();
    }
}