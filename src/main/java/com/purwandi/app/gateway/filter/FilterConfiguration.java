package com.purwandi.app.gateway.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationRegister(AuthenticationFilter filter) {
        FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationRegister(AuthorizationFilter filter) {
        FilterRegistrationBean<AuthorizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/v2/*");
        registration.setOrder(2);
        registration.addInitParameter("scope", "openid email");
        // registration.addInitParameter("scope", "role-management-admin role-management-editor");
        return registration;
    }
}
