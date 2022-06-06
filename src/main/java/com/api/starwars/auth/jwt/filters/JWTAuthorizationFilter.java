package com.api.starwars.auth.jwt.filters;

import com.api.starwars.common.exceptions.http.HttpInternalServerErrorException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsService userDetailsService) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");

        if (header == null) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }


    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String secret = System.getenv("AUTHORIZATION_SECRET");
        if (StringUtils.isEmpty(secret)){
            log.error("Incapaz de autenticar em razao de nao encontrar authorizaion secret");
            throw new HttpInternalServerErrorException("Authorization secret not found");
        }

        if (token != null) {
            String user = JWT.require(Algorithm.HMAC512(secret.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();

            if (user != null) {
                UserDetails applicationUser = userDetailsService.loadUserByUsername(user);
                return new UsernamePasswordAuthenticationToken(applicationUser.getUsername(), null, applicationUser.getAuthorities());
            }

            return null;
        }

        return null;
    }
}