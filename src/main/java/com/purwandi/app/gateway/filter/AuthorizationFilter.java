package com.purwandi.app.gateway.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AuthorizationFilter implements Filter {

    private String[] scopes;
    private String[] roles;

    @Override
    public void init(FilterConfig args) throws ServletException {
        String scope = args.getInitParameter("scope");
        String role = args.getInitParameter("role");
        log.atInfo()
            .addKeyValue("scope", scope)
            .addKeyValue("role", role)
            .log("filter : authorization initiliaze");

        this.scopes = scope.split(" ");
        this.roles = scope.split(" ");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException  {
        log.info("filter authz");
        log.info((String) request.getAttribute("scope"));
        String[] tokenScope = ((String) request.getAttribute("scope")).split(" ");

        // TODO allowed token scope
        for (String tkscope : tokenScope) {
            if (Arrays.asList(scopes).contains(tkscope)) {

            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
