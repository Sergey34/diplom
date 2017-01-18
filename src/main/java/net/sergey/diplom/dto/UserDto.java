package net.sergey.diplom.dto;

import net.sergey.diplom.domain.user.UserRole;

import java.util.Set;

public class UserDto {
    private String userName;
    private Set<UserRole> userRoles;

    public UserDto() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
