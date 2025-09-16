package kr.modusplant.domains.identity.framework.legacy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.identity.framework.legacy.handler.*;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.legacy.modules.jwt.app.service.TokenProvider;
import kr.modusplant.legacy.modules.jwt.persistence.repository.TokenRedisRepository;
import kr.modusplant.domains.identity.framework.legacy.DefaultAuthProvider;
import kr.modusplant.domains.identity.framework.legacy.DefaultAuthenticationEntryPoint;
import kr.modusplant.domains.identity.framework.legacy.DefaultUserDetailsService;
import kr.modusplant.domains.identity.framework.legacy.filter.EmailPasswordAuthenticationFilter;
import kr.modusplant.domains.identity.framework.legacy.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.Validator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.debug.enabled}")
    private Boolean debugEnabled;

    private final AuthenticationConfiguration authConfiguration;
    private final DefaultUserDetailsService defaultUserDetailsService;
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final TokenApplicationService tokenApplicationService;
    private final SiteMemberValidationService memberValidationService;
    private final SiteMemberRepository memberRepository;
    private final Validator validator;
    private final TokenRedisRepository tokenRedisRepository;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(debugEnabled);
    }

    @Bean
    public DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint() {
        return new DefaultAuthenticationEntryPoint(objectMapper); }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public DefaultAuthProvider siteMemberAuthProvider() {
        return new DefaultAuthProvider(defaultUserDetailsService, passwordEncoder());
    }

    @Bean
    public ForwardRequestLoginSuccessHandler forwardRequestLoginSuccessHandler() {
        return new ForwardRequestLoginSuccessHandler(memberRepository, memberValidationService, tokenApplicationService);
    }

    @Bean
    public WriteResponseLoginFailureHandler writeResponseLoginFailureHandler() {
        return new WriteResponseLoginFailureHandler(objectMapper);
    }

    @Bean
    public JwtClearingLogoutHandler JwtClearingLogoutHandler() {
        return new JwtClearingLogoutHandler(tokenApplicationService); }

    @Bean
    public ForwardRequestLogoutSuccessHandler normalLogoutSuccessHandler() {
        return new ForwardRequestLogoutSuccessHandler(objectMapper); }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(HttpSecurity http) {
        try {
            return new JwtAuthenticationFilter(tokenProvider, defaultAuthenticationEntryPoint(), tokenRedisRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter(HttpSecurity http) {
        try {
            EmailPasswordAuthenticationFilter filter = new EmailPasswordAuthenticationFilter(
                    objectMapper, validator, authenticationManager());

            filter.setAuthenticationManager(authenticationManager());
            filter.setAuthenticationSuccessHandler(forwardRequestLoginSuccessHandler());
            filter.setAuthenticationFailureHandler(writeResponseLoginFailureHandler());

            return filter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public DefaultAccessDeniedHandler defaultAccessDeniedHandler() {
        return new DefaultAccessDeniedHandler(objectMapper);
    }

    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .addFilterBefore(emailPasswordAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(http), EmailPasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/communication/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/terms/**").permitAll()
                        .requestMatchers("/api/members/verify-email/send/**").permitAll()
                        .requestMatchers("/api/auth/kakao/social-login").permitAll()
                        .requestMatchers("/api/auth/google/social-login").permitAll()
                        .requestMatchers("/api/members/register").permitAll()
                        .requestMatchers("/api/monitor/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(siteMemberAuthProvider())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .clearAuthentication(true)
                        .addLogoutHandler(JwtClearingLogoutHandler())
                        .logoutSuccessHandler(normalLogoutSuccessHandler()))
                .exceptionHandling(eh ->
                        eh.authenticationEntryPoint(defaultAuthenticationEntryPoint())
                                .accessDeniedHandler(defaultAccessDeniedHandler())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(300)) // 5분
                        .contentTypeOptions(Customizer.withDefaults())
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("https://specified-jaquith-modusplant-0c942371.koyeb.app");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}