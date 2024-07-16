package com.osaebros.eventplanner.filter;

import com.osaebros.eventplanner.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
@Component
@Slf4j
public class InterceptRequestAccessTokenFilter extends OncePerRequestFilter {

    private static final String MOCK_AUTH_GROUP = "EVENT";
    private static final String INTERNAL_MOCK_JWT_TOKEN = "MOCK_TOKEN";
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


//        String token = jwtUtil.extractTokenFromHeader(request);
//        if (isMockJWTTokenValid(token)) {
//            Authentication origAuthentication = SecurityContextHolder.getContext().getAuthentication();
//            Authentication newAuthentication = createNewAuthentication(origAuthentication);
//            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
//        }
        filterChain.doFilter(request, response);
    }

    private Authentication createNewAuthentication(Authentication origAuthentication) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + MOCK_AUTH_GROUP));
        Object principal = origAuthentication != null ? origAuthentication.getPrincipal() : null;
        Object details = origAuthentication != null ? origAuthentication.getDetails() : null;
        return new UsernamePasswordAuthenticationToken(principal, details, authorities);
    }

    Boolean isMockJWTTokenValid(String accessToken) {
        return Objects.nonNull(accessToken) && accessToken.equalsIgnoreCase(INTERNAL_MOCK_JWT_TOKEN);
    }
}