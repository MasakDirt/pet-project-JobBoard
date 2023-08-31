package com.board.job.component;

import com.board.job.service.UserService;
import com.board.job.util.JwtUtils;
import com.github.javaparser.quality.NotNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final String HEADER_PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                 @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (hasAuthorizationBearer(request)) {
            String token = getAccessToken(request);
            if (jwtUtils.isValidToken(token)) {
                setAuthContext(token, request);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return Objects.nonNull(header) && header.startsWith(HEADER_PREFIX);
    }

    private String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(HEADER_PREFIX.length());
    }

    private void setAuthContext(String token, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                getUsernamePasswordAuthenticationToken(token);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String token) {
        var userDetails = userService.readByEmail(jwtUtils.getSubject(token));

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                null, userDetails.getAuthorities());
    }
}
