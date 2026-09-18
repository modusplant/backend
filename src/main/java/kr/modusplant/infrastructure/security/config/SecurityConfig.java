package kr.modusplant.infrastructure.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.infrastructure.jwt.framework.outbound.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtCookieProvider;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.DefaultAuthProvider;
import kr.modusplant.infrastructure.security.DefaultAuthenticationEntryPoint;
import kr.modusplant.infrastructure.security.DefaultUserDetailsService;
import kr.modusplant.infrastructure.security.filter.EmailPasswordAuthenticationFilter;
import kr.modusplant.infrastructure.security.filter.JwtAuthenticationFilter;
import kr.modusplant.infrastructure.security.filter.SecurityExceptionHandlingFilter;
import kr.modusplant.infrastructure.security.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.validation.Validator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 인증이 필요 없는(permit-all) 엔드포인트.
     */
    public static final Map<HttpMethod, List<String>> PUBLIC_ENDPOINTS = Map.of(
            HttpMethod.GET, List.of(
                    "/api/v1/terms",
                    "/api/v1/communication/primary-categories",
                    "/api/v1/communication/primary-categories/**",
                    "/api/v1/search/plant/korean-name",
                    "/api/v1/communication/comments/post/**",
                    "/api/v1/members/check/nickname/**",
                    "/api/v1/search/posts",
                    "/api/v1/communication/posts/**",
                    "/actuator/prometheus"
            ),
            HttpMethod.POST, List.of(
                    "/api/members/register",
                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/members/verify-email/send",
                    "/api/members/verify-email",
                    "/api/auth/reset-password-request/send",
                    "/api/auth/reset-password-request/verify/email",
                    "/api/auth/reset-password-request/verify/input",
                    "/api/auth/token/refresh",
                    "/api/v1/local/auth/social-login/**",
                    "/api/v1/auth/social-login/**",
                    "/api/v1/auth/social-signup",
                    "/api/v1/auth/social-link"
            ),
            HttpMethod.DELETE, List.of(
                    "/api/v1/auth/social-connect"
            ),
            HttpMethod.PATCH, List.of(
                    "/api/v1/communication/posts/*/views"
            )
    );

    @Value("${security.debug.enabled}")
    private Boolean debugEnabled;

    private final AuthenticationConfiguration authConfiguration;
    private final DefaultUserDetailsService defaultUserDetailsService;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;
    private final JwtCookieProvider cookieProvider;
    private final TokenService tokenService;
    private final MemberJpaRepository memberRepository;
    private final Validator validator;
    private final AccessTokenRedisRepository tokenRedisRepository;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(debugEnabled);
    }

    @Bean
    public DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint() {
        return new DefaultAuthenticationEntryPoint(objectMapper); }

    @Bean
    @Qualifier("bcryptPasswordEncoder")
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Qualifier("pbkdf2PasswordEncoder")
    public PasswordEncoder pbkdf2PasswordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public DefaultAuthProvider siteMemberAuthProvider() {
        return new DefaultAuthProvider(defaultUserDetailsService, bcryptPasswordEncoder());
    }

    @Bean
    public WriteResponseLoginSuccessHandler forwardRequestLoginSuccessHandler() {
        return new WriteResponseLoginSuccessHandler(memberRepository, tokenService, cookieProvider, objectMapper);
    }

    @Bean
    public WriteResponseLoginFailureHandler writeResponseLoginFailureHandler() {
        return new WriteResponseLoginFailureHandler(objectMapper);
    }

    @Bean
    public JwtClearingLogoutHandler JwtClearingLogoutHandler() {
        return new JwtClearingLogoutHandler(tokenService); }

    @Bean
    public WriteResponseLogoutSuccessHandler normalLogoutSuccessHandler() {
        return new WriteResponseLogoutSuccessHandler(objectMapper); }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(HttpSecurity http) {
        try {
            return new JwtAuthenticationFilter(tokenProvider, tokenRedisRepository);
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
    public SecurityExceptionHandlingFilter securityExceptionHandlingFilter() {
        return new SecurityExceptionHandlingFilter(objectMapper, defaultAuthenticationEntryPoint());
    }

    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http,
                                            @Qualifier("corsConfigurationSource")
                                            CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(securityExceptionHandlingFilter(), LogoutFilter.class)
                .addFilterBefore(emailPasswordAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(http), EmailPasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/admin/**").hasAuthority("ADMIN");
                    PUBLIC_ENDPOINTS.forEach((httpMethod, patterns) ->
                            auth.requestMatchers(httpMethod, patterns.toArray(new String[0])).permitAll());
                    auth.anyRequest().authenticated();
                })
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
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(300)) // 5분
                        .contentTypeOptions(Customizer.withDefaults())
                );
        return http.build();
    }

    @Bean
    @Profile({"local", "dev"})
    @Qualifier("corsConfigurationSource")
    public CorsConfigurationSource localDevCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(Duration.ofMinutes(10));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    @Profile("prod")
    @Qualifier("corsConfigurationSource")
    public CorsConfigurationSource prodCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://modusplant.kr", "https://www.modusplant.kr"));
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}