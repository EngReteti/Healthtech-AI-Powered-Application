package com.amason.hospitalinventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

// OncePerRequestFilter means: "run this exactly once for every single 
// incoming request, before it reaches any Controller" - this is what 
// lets us check the token BEFORE any of our actual business logic runs
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Real tokens arrive in a header like: "Authorization: Bearer eyJhbGc..."
        String authHeader = request.getHeader("Authorization");

        // If there's no token at all, or it doesn't start with "Bearer ", 
        // just let the request continue WITHOUT marking anyone as logged in - 
        // SecurityConfig will decide later whether this specific endpoint 
        // actually requires login or not
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip off "Bearer " to get just the raw token text
        String token = authHeader.substring(7);

        try {
            // Ask JwtUtil to verify the token's seal and pull out who it belongs to
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // Build a Spring Security "authentication" object - this is 
            // literally what tells the rest of the app "this request is 
            // genuinely from this email, with this role, and it's verified"
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            // Register it for the rest of THIS request only - it's not 
            // remembered anywhere, so this check runs fresh every time
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            // TEMPORARY: print the real reason token validation is failing, 
            // so we can diagnose - we'll remove this print once fixed
            System.out.println("JWT validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
