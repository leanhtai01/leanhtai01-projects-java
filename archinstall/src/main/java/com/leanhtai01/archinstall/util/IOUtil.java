package com.leanhtai01.archinstall.util;

public final class IOUtil {
    private IOUtil() {
    }

    public static String readPassword(String firstPrompt, String secondPrompt, String errorMessage) {
        System.console().printf(firstPrompt);
        String password = String.valueOf(System.console().readPassword());

        System.console().printf(secondPrompt);
        String reEnterPassword = String.valueOf(System.console().readPassword());

        while (!password.equals(reEnterPassword)) {
            System.console().printf(errorMessage);

            System.console().printf(firstPrompt);
            password = String.valueOf(System.console().readPassword());

            System.console().printf(secondPrompt);
            reEnterPassword = String.valueOf(System.console().readPassword());
        }

        return password;
    }
}
