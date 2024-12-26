package com.purwandi.app.gateway.filter;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationFilter implements Filter {

    private final String jwksUrl = "http://localhost:8080/realms/one-realm/protocol/openid-connect/certs";
    private final String jwksIssuer = "http://localhost:8080/realms/one-realm";

    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @Override
    public void init(FilterConfig args) throws ServletException {
        log.info("filters init");

        jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(JOSEObjectType.JWT));

        try {
            final long ttl = 60*60*1000; // 1 hour
            final long refreshTimeout = 60*1000; // 1 minute
            final long outageTTL = 4*60*60*1000; // 4 hours
            final int leeway = 5*60; // 5 minute

            JWKSource<SecurityContext> keySource = JWKSourceBuilder.create(new URL(jwksUrl))
                .retrying(true)
                .cache(ttl, refreshTimeout)
                .outageTolerant(outageTTL)
                .build();
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);

            DefaultJWTClaimsVerifier<SecurityContext> verifier = new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(jwksIssuer).build(),
                new HashSet<>(Arrays.asList(
                    JWTClaimNames.ISSUER,
                    JWTClaimNames.SUBJECT,
                    JWTClaimNames.ISSUED_AT,
                    JWTClaimNames.EXPIRATION_TIME
                ))
            );
            verifier.setMaxClockSkew(-leeway);

            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(verifier);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("fo filters");

        HttpServletRequest req = (HttpServletRequest) request;

        log.atInfo()
            .addKeyValue("key", "value")
            .log("Starting a transaction for req : {}",req.getRequestURI());

        final String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        final String token = header.split(" ")[1].trim();
        SecurityContext ctx = null; // optional context parameter, not required here
        JWTClaimsSet claims;

        try {
            claims = jwtProcessor.process(token, ctx);
        } catch (ParseException | BadJOSEException e) {
            // Invalid token
            System.err.println(e.getMessage());
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        } catch (JOSEException e) {
            // Key sourcing failed or another internal exception
            System.err.println(e.getMessage());
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        log.info(claims.toJSONObject().toString());

        chain.doFilter(request, response);


        log.atInfo()
            .addKeyValue("key", "value")
            .log("Committing a transaction for req : {}",req.getRequestURI());
    }

    @Override
    public void destroy() {}

}
