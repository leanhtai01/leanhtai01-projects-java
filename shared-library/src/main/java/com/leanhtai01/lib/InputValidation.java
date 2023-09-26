package com.leanhtai01.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class InputValidation {
    private InputValidation() {
    }

    public static int isValidInteger(String input, int from, int to) {
        int number = Integer.parseInt(input);

        if (number < from || number > to) {
            throw new IllegalArgumentException("The input must be in range [%d, %d]%n".formatted(from, to));
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

    public static List<Integer> parseRangeIntegerChoice(String input, int minChoice, int maxChoice) {
        List<Integer> choices = new ArrayList<>();
        Pattern enumeratePattern = Pattern.compile("^\\d[\\s\\d]*");
        Pattern rangePattern = Pattern.compile("^\\d-\\d");

        if (enumeratePattern.matcher(input).matches()) {
            choices = new ArrayList<>(
                    Set.copyOf(Arrays.asList(input.split(" ")).stream().map(Integer::parseInt).toList()));
        } else if (rangePattern.matcher(input).matches()) {
            String[] minMax = input.split("-");
            int min = Integer.parseInt(minMax[0]);
            int max = Integer.parseInt(minMax[1]);

            if (min < max) {
                for (int i = min; i <= max; i++) {
                    choices.add(i);
                }
            }
        }

        for (Integer choice : choices) {
            if (choice < minChoice || choice > maxChoice) {
                choices = List.of();
                break;
            }
        }

        return choices;
    }
}
