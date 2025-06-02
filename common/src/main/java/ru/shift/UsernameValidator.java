package ru.shift;

import java.util.regex.Pattern;

public class UsernameValidator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;

    public static boolean isInvalid(String username) {
        if (username == null || username.isEmpty()) {
            return true;
        }
        if (username.length() < MIN_LENGTH || username.length() > MAX_LENGTH) {
            return true;
        }
        return !USERNAME_PATTERN.matcher(username).matches();
    }
}
