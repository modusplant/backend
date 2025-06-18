package kr.modusplant.global.middleware.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.advice.GlobalExceptionHandler;
import kr.modusplant.global.middleware.security.SiteMemberAuthProvider;
import kr.modusplant.global.middleware.security.SiteMemberUserDetailsService;
import kr.modusplant.global.middleware.security.filter.EmailPasswordAuthenticationFilter;
import kr.modusplant.global.middleware.security.handler.JwtClearingLogoutHandler;
import kr.modusplant.global.middleware.security.handler.WriteResponseLoginFailureHandler;
import kr.modusplant.global.middleware.security.handler.ForwardRequestLoginSuccessHandler;
import kr.modusplant.global.middleware.security.handler.ForwardRequestLogoutSuccessHandler;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final GlobalExceptionHandler globalExceptionHandler;
    private final SiteMemberUserDetailsService memberUserDetailsService;
    private final ObjectMapper objectMapper;
    private final TokenApplicationService tokenApplicationService;
    private final SiteMemberRepository memberRepository;

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
    public ForwardRequestLoginSuccessHandler normalLoginSuccessHandler() {
        return new ForwardRequestLoginSuccessHandler(memberRepository, tokenApplicationService);
    }

    @Bean
    public JwtClearingLogoutHandler JwtClearingLogoutHandler() {
        return new JwtClearingLogoutHandler(tokenApplicationService); }

    @Bean
    public WriteResponseLoginFailureHandler normalLoginFailureHandler() {
        return new WriteResponseLoginFailureHandler(objectMapper);
    }

    @Bean
    public ForwardRequestLogoutSuccessHandler normalLogoutSuccessHandler() {
        return new ForwardRequestLogoutSuccessHandler(objectMapper); }

    @Bean
    public EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter(HttpSecurity http) {
        try {
            EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter = new EmailPasswordAuthenticationFilter(
                    new ObjectMapper(), authenticationManager());

            emailPasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
            emailPasswordAuthenticationFilter.setAuthenticationSuccessHandler(normalLoginSuccessHandler());
            emailPasswordAuthenticationFilter.setAuthenticationFailureHandler(normalLoginFailureHandler());

            return emailPasswordAuthenticationFilter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .addFilterBefore(emailPasswordAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/conversation/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/qna/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tip/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/terms/**").permitAll()
                        .requestMatchers("/api/members/verify-email/send/**").permitAll()
                        .requestMatchers("/api/auth/kakao/social-login").permitAll()
                        .requestMatchers("/api/auth/google/social-login").permitAll()
                        .requestMatchers("/api/members/register").permitAll()
                        .requestMatchers("/api/example").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(siteMemberAuthProvider())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .clearAuthentication(true)
                        .addLogoutHandler(JwtClearingLogoutHandler())
                        .logoutSuccessHandler(normalLogoutSuccessHandler()))
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