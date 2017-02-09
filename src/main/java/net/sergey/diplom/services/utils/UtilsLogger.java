package net.sergey.diplom.services.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UtilsLogger {
    public static String getStaticClassName() {
        String className;
        try {
            throw new Exception();
        } catch (Exception e) {
            className = e.getStackTrace()[1].getClassName();
        }
        return className;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
