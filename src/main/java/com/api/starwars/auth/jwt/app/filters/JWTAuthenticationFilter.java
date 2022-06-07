package com.api.starwars.auth.jwt.app.filters;

import com.api.starwars.auth.jwt.domain.view.UserJson;
import com.api.starwars.common.exceptions.http.HttpInternalServerErrorException;
import com.api.starwars.common.exceptions.http.HttpUnauthorizedException;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        try {
            UserJson credentials = new ObjectMapper()
                    .readValue(req.getInputStream(), UserJson.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            null)
            );

        } catch (IOException e) {
            logger.warn("Usuario nao possui as credenciais corretas");
            throw new HttpUnauthorizedException("Invalid credentials");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {

        String secret = System.getenv("AUTHORIZATION_SECRET");
        if (StringUtils.isEmpty(secret)){
            log.error("Incapaz de autenticar em razao de nao encontrar authorizaion secret");
            throw new HttpInternalServerErrorException("Authorization secret not found");
        }

        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .sign(HMAC512(secret));

        res.addHeader("Authorization", token);
        res.getWriter().write(token);
        res.getWriter().flush();
    }
}