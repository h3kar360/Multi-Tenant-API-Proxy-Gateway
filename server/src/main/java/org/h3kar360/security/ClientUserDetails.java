package org.h3kar360.security;

import org.h3kar360.model.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ClientUserDetails implements UserDetails {
    private final Client client;

    public ClientUserDetails(Client client) {
        this.client = client;
    }

    @Override
    public String getUsername() {
        return client.getClientEmail();
    }

    @Override
    public String getPassword() {
        return client.getClientPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return client.isActive();
    }

    public Long getClientId() {
        return client.getId();
    }

    public Client getClient() {
        return client;
    }
}
