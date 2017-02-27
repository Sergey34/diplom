package net.sergey.diplom.services.utils;

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
}
