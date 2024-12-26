package com.purwandi.app.gateway.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
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
        String[] tokenScope = ((String) request.getAttribute("scope")).split(" ");

        for (String tkscope : tokenScope) {
            if (Arrays.asList(scopes).contains(tkscope)) {
                chain.doFilter(request, response);
                return;
            }
        }

        ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
        return;
    }

    @Override
    public void destroy() {}
}
