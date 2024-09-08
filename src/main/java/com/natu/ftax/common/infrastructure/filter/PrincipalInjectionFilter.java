package com.natu.ftax.common.infrastructure.filter;

import com.natu.ftax.client.ClientRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

public class PrincipalInjectionFilter extends OncePerRequestFilter {

    private final ClientRepo clientRepo;
    private final Environment env;

    public PrincipalInjectionFilter(ClientRepo clientRepo, Environment env) {
        this.clientRepo = clientRepo;
        this.env = env;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = null;
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            // Basic Auth for dev environment
            email = getEmailInBasicAuth(request, email);
        } else {
            email = getEmailInCookies(request, email);

        }
        if (email == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var client = clientRepo.findById(email);
        if (client.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(client.get(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private static String getEmailInCookies(HttpServletRequest request, String email) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("email".equals(cookie.getName())) {
                    email = cookie.getValue();
                }
            }
        }
        email = URLDecoder.decode(email, StandardCharsets.UTF_8);
        return email;
    }

    private static String getEmailInBasicAuth(HttpServletRequest request, String email) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            if (values.length == 2) {
                email = values[0];
            }
        }
        return email;
    }
}