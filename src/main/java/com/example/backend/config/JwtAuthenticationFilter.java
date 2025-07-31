package com.example.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.services.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No Authorization header or bad format for path: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("🔍 Extracted email: " + userEmail + " for path: " + request.getRequestURI());

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (userDetails == null) {
                    System.out.println("❌ User not found: " + userEmail);
                    filterChain.doFilter(request, response);
                    return;
                }
                
                boolean tokenValid = jwtService.isTokenValid(jwt, userDetails);
                boolean blacklisted = tokenBlacklistService.isTokenBlacklisted(jwt);

                System.out.println("✅ Token valid: " + tokenValid);
                System.out.println("🚫 Token blacklisted: " + blacklisted);
                System.out.println("👤 User enabled: " + userDetails.isEnabled());

                if (tokenValid && !blacklisted && userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ User authenticated successfully: " + userEmail);
                } else {
                    System.out.println("❌ Authentication failed - Token invalid: " + !tokenValid + 
                                     ", Blacklisted: " + blacklisted + 
                                     ", User disabled: " + !userDetails.isEnabled());
                }
            } else {
                System.out.println("⛔ Email is null or already authenticated for: " + userEmail);
            }
        } catch (Exception e) {
            System.out.println("❌ JWT Authentication error: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

}
