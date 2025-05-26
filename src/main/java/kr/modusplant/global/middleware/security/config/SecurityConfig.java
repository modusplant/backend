package kr.modusplant.global.middleware.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.global.advice.GlobalExceptionHandler;
import kr.modusplant.global.middleware.security.JsonEmailAuthFilter;
import kr.modusplant.global.middleware.security.SiteMemberAuthProvider;
import kr.modusplant.global.middleware.security.SiteMemberUserDetailsService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.debug.enabled}")
    private Boolean debugEnabled;

    private final AuthenticationConfiguration authConfiguration;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final SiteMemberUserDetailsService memberUserDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(debugEnabled);
    }

    @Bean
    public SecurityContextHolder securityContextHolder() { return new SecurityContextHolder(); }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SiteMemberAuthProvider siteMemberAuthProvider() {
        return new SiteMemberAuthProvider(memberUserDetailsService, passwordEncoder());
    }

    @Bean
    public JsonEmailAuthFilter jsonEmailAuthFilter(HttpSecurity http) {
        try {
            JsonEmailAuthFilter jsonEmailAuthFilter = new JsonEmailAuthFilter(
                    new ObjectMapper(), authenticationManager());
            jsonEmailAuthFilter.setAuthenticationManager(authenticationManager());
            return jsonEmailAuthFilter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .addFilterBefore(jsonEmailAuthFilter(http), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/terms").permitAll()
                        .requestMatchers("/api/members/**").permitAll()
                        .requestMatchers("/*/social-login").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers("/auth/token/refresh").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(siteMemberAuthProvider())
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