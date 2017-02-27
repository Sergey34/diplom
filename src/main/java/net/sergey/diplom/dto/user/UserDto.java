package net.sergey.diplom.dto.user;

import net.sergey.diplom.domain.user.Authorities;

import java.util.List;

public class UserDto {
    private String userName;
    private List<Authorities> userRoles;

    public UserDto() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Authorities> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Authorities> userRoles) {
        this.userRoles = userRoles;
    }
}
