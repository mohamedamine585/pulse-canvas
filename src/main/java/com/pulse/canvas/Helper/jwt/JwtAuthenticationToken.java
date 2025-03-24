package com.pulse.canvas.Helper.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Claims claims; // Holds the JWT claims

    public JwtAuthenticationToken(Claims claims) {
        super(null); // Pass null for the authorities as they can be set later
        this.claims = claims;
        setAuthenticated(true); // Set authenticated status
    }

    @Override
    public Object getCredentials() {
        return null; // Return null as credentials are not needed in this context
    }

    @Override
    public Object getPrincipal() {
        return claims; // Return claims as the principal
    }


    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = claims.get("roles", List.class);

        for (String roleName : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        }

        return authorities;
    }

}
