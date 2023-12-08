package com.board.job.security;

import com.board.job.model.entity.CustomOAuth2User;
import com.board.job.service.CustomOAuth2UserService;
import com.board.job.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    private final CustomOAuth2UserService oAuthUserService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(antMatcher("/api/auth/**"), antMatcher("/oauth2/**")).permitAll()
                        .anyRequest()
                        .authenticated()
                );

        http.formLogin(login -> login
                .loginPage("/api/auth/login")
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    String email = authentication.getName();
                    var user = userService.readByEmail(email);
                    response.sendRedirect("/api/users/" + user.getId());
                })
                .failureUrl("/api/auth/login?error")
                .permitAll()
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/api/auth/login")
                .userInfoEndpoint(oAuthUserService)
                .successHandler((request, response, authentication) -> {
                            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                            var user = userService.processOAuthPostLogin(oauthUser);
                            response.sendRedirect("/api/users/" + user.getId());
                        }
                )
        );

        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/api/auth/login")
                .permitAll()
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
