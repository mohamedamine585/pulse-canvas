package com.pulse.canvas.Helper.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY="JcIdM7U+Q3Bp1L+BBlUmg3nQh+v60jKejqr1H64EyakcW6TzFeOrIa4is8AcHeaQC4R5TglzKiEQtsVAoYPKmdot9EgxBugTxhYBrEf2qfMZupXCMG8Rb4BLvr8hyiX7YrjJoN/ZhJACGoyqF1HquBWTTHmqFSmMWPmuqatVCzA+knyWsUdXkzR1FS3SFjNmFpAp4VgInet1NKb+MU1wdE9Bpd1r67EZwWSGilD3N3bZBF2tteUVqeC+X5uRQttAqlsqxFEu0/CL0qggISaUJTz/g7pz/fXYvEpQTJI+XMcOxSeJPLpdv+4ax+jW7qCW35wrrledqN/cZ6E1XwyNOyYdFu5+jrT6y7zxvFSu1qo=";


    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            System.out.println(header.substring(7));
            return header.substring(7);
        }
        return null;
    }


    private Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes())
                .build().parseClaimsJws(token).getBody();
    }


    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }



    private boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            return true; // Token is valid
        } catch (Exception e) {
            // Catch and log errors, if any
            System.out.println(e.getMessage());
            return false; // Token is invalid
        }
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null && validateToken(token) && !isTokenExpired(token)) {
            Claims claims = getClaims(token);

            // Create the authentication object
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(claims);
            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
