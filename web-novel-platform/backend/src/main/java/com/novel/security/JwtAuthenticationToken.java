package com.novel.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken implements Authentication {
    
    private final JwtUserDetails principal;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private boolean authenticated = true;
    
    public JwtAuthenticationToken(JwtUserDetails principal, 
                                 Collection<? extends GrantedAuthority> authorities, 
                                 String name) {
        this.principal = principal;
        this.authorities = authorities;
        this.name = name;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public Object getCredentials() {
        return null;
    }
    
    @Override
    public Object getDetails() {
        return null;
    }
    
    @Override
    public Object getPrincipal() {
        return principal;
    }
    
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
