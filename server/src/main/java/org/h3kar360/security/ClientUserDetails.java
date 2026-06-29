package org.h3kar360.security;

import org.h3kar360.model.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record ClientUserDetails(Client client) implements UserDetails {
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
        return List.of();
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
}
