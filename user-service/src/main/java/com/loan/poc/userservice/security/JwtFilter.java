package com.loan.poc.userservice.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.loan.poc.userservice.service.MyUserDetailsService;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        // 1. Get the "Authorization" header from the request
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 2. Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            
            // Extract token by removing "Bearer "
            jwtToken = authHeader.substring(7);

            // 3. Extract username from the token
            username = jwtUtil.extractUsername(jwtToken);
        }

        /**
         * 4. If we have a username AND user not already authenticated,
         *    then validate the token.
         */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Load user details from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Validate the JWT token
            if (jwtUtil.validateToken(jwtToken, userDetails)) {

                // 7. Create authentication object manually
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // 8. Tell Spring Security: user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}

