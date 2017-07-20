package net.sergey.diplom.services.usermanagerservice;

import net.sergey.diplom.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
