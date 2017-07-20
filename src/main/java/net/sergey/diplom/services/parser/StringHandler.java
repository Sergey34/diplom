package net.sergey.diplom.services.parser;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
@Component
public class StringHandler {
    public StringHandler() {
    }

    public String createStringByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public boolean isDoubleStr(String str) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
