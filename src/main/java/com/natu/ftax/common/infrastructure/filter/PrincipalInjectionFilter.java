package com.natu.ftax.common.infrastructure.filter;

import com.natu.ftax.client.ClientRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class PrincipalInjectionFilter extends OncePerRequestFilter {

    private final ClientRepo clientRepo;

    public PrincipalInjectionFilter(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = null;


        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("email".equals(cookie.getName())) {
                    email = cookie.getValue();
                }
            }
        }
        if (email == null) {
            filterChain.doFilter(request, response);
            return;
        }

        email = URLDecoder.decode(email, StandardCharsets.UTF_8);
        var client = clientRepo.findById(email);
        if (client.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(client.get(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}