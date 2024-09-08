package com.natu.ftax.common.infrastructure;

import com.natu.ftax.auth.AuthRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final AuthRepo authRepo;

    public CustomMethodSecurityExpressionHandler(AuthRepo authRepo) {
        this.authRepo = authRepo;
    }

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }

    private class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
        private Object filterObject;
        private Object returnObject;
        private Object target;

        public CustomMethodSecurityExpressionRoot(Authentication authentication) {
            super(authentication);
        }

        public boolean isConnected() {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Cookie[] cookies = request.getCookies();
            String email = null;
            String hash = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("email".equals(cookie.getName())) {
                        email = cookie.getValue();
                    }
                    if ("hash".equals(cookie.getName())) {
                        hash = cookie.getValue();
                    }
                }
            }
            if (email == null || hash == null) {
                return false;
            }
            email = URLDecoder.decode(email, StandardCharsets.UTF_8);
            if (!authRepo.existsByClientEmailAndHashAndVerified(email, hash, true)) {
                return false;
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null,
                    Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;

        }

        @Override
        public void setFilterObject(Object filterObject) {
            this.filterObject = filterObject;
        }

        @Override
        public Object getFilterObject() {
            return filterObject;
        }

        @Override
        public void setReturnObject(Object returnObject) {
            this.returnObject = returnObject;
        }

        @Override
        public Object getReturnObject() {
            return returnObject;
        }

        @Override
        public Object getThis() {
            return target;
        }

        void setThis(Object target) {
            this.target = target;
        }
    }
}