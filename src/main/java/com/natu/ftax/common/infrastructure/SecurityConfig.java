package com.natu.ftax.common.infrastructure;

import com.natu.ftax.client.ClientRepo;
import com.natu.ftax.common.infrastructure.filter.PrincipalInjectionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ClientRepo clientRepo;
    private final Environment environment;

    public SecurityConfig(ClientRepo clientRepo, Environment environment) {
        this.clientRepo = clientRepo;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new PrincipalInjectionFilter(clientRepo, environment), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
//                .cors().disable(); // Disable CORS restrictions

        return http.build();

    }

}