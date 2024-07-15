package site.billingwise.api.serverapi.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import site.billingwise.api.serverapi.global.jwt.JwtAccessDeniedHandler;
import site.billingwise.api.serverapi.global.jwt.JwtAuthenticationEntryPoint;
import site.billingwise.api.serverapi.global.jwt.JwtFilter;
import site.billingwise.api.serverapi.global.jwt.JwtProvider;

import java.util.Collections;

import static site.billingwise.api.serverapi.global.config.WebConfig.ALLOWED_ORIGIN_LIST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private static final String[] PERMIT_LIST = {
        "/actuator/health",
        "/api/v1/auth/**",
        "/api/v1/easy-consent/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable());
        http
                .formLogin((auth) -> auth.disable());
        http
                .httpBasic((auth) -> auth.disable());
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()));
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(PERMIT_LIST).permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        for (String url : ALLOWED_ORIGIN_LIST) {
            config.addAllowedOriginPattern(url);
        }
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
