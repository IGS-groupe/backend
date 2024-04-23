package com.example.backend.JWT;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.backend.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Extract username and password from request
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String token = JWT.create()
            .withSubject(((User) authResult.getPrincipal()).getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 864_000_000)) // 10 days
            .sign(Algorithm.HMAC512("secret")); // Use a secure secret
        response.addHeader("Authorization", "Bearer " + token);
    }
}
