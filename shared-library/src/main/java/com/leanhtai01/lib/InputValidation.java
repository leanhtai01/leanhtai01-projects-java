package com.leanhtai01.lib;

public final class InputValidation {
    private InputValidation() {
    }

    public static int isValidInteger(String input, int from, int to) {
        int number = Integer.parseInt(input);

        if (number < from || number > to) {
            throw new IllegalArgumentException("The input must be in range [%d, %d]".formatted(from, to));
        }

        return number;
    }

    public static String readPasswordFromConsole(String firstPrompt, String secondPrompt, String errorMessage) {
        System.console().printf(firstPrompt);
        String password = String.valueOf(System.console().readPassword());

        System.console().printf(secondPrompt);
        String reenterPassword = String.valueOf(System.console().readPassword());

        while (!password.equals(reenterPassword)) {
            System.console().printf(errorMessage);
            System.console().printf(firstPrompt);
            password = String.valueOf(System.console().readPassword());

            System.console().printf(secondPrompt);
            reenterPassword = String.valueOf(System.console().readPassword());
        }

        return password;
    }

    public static int chooseIntegerOption(Runnable displayMenu, int minChoice, int maxChoice) {
        boolean isValidChoice = false;
        int choice = minChoice;

        while (!isValidChoice) {
            displayMenu.run();
            try {
                String input = System.console().readLine();
                choice = isValidInteger(input, minChoice, maxChoice);
                isValidChoice = true;
            } catch (IllegalArgumentException e) {
                isValidChoice = false;
                System.console().printf(e.getMessage());
            }
        }

        return choice;
    }
}
