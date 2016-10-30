package net.sergey.diplom.service.utils;

import net.sergey.diplom.domain.user.UserRole;

import java.util.*;


public class UtilRoles {
    private static final Map<String, UserRole> userRoleMap;

    static {
        userRoleMap = new HashMap<>();
    }

    public static Set<UserRole> findUserRoleByName(List<String> roleNames) {
        Set<UserRole> userRoles = new HashSet<>();
        for (String roleName : roleNames) {
            userRoles.add(userRoleMap.get(roleName));
        }
        return userRoles;
    }

    public static void init(List<UserRole> userRoles) {
        for (UserRole userRole : userRoles) {
            userRoleMap.put(userRole.getRole(), userRole);
        }
    }
}
