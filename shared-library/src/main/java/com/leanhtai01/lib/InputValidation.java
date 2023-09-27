package com.leanhtai01.lib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class InputValidation {
    private InputValidation() {
    }

    public static void displayMenu(List<String> menu, String promptMessage) {
        for (int i = 0; i < menu.size(); i++) {
            System.console().printf("%d. %s%n", i + 1, menu.get(i));
        }
        System.console().printf(promptMessage);
    }

    public static int parseIntegerChoice(String input, int from, int to) {
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
                choice = parseIntegerChoice(input, minChoice, maxChoice);
                isValidChoice = true;
            } catch (IllegalArgumentException e) {
                isValidChoice = false;
                System.console().printf(e.getMessage());
            }
        }

        return choice;
    }

    public static Set<Integer> chooseRangeIntegerOption(List<String> menu, String promptMessage,
            Set<Integer> choices, int minChoice, int maxChoice, int exitOption) {
        displayMenu(menu, promptMessage);
        String input = System.console().readLine();
        while (!input.trim().equals(String.valueOf(exitOption))) {
            Set<Integer> newChoices = parseRangeIntegerChoice(input, minChoice, maxChoice);
            Set<Integer> removedChoices = getRemovedChoices(choices, newChoices);
            choices.removeAll(removedChoices);
            newChoices.removeAll(removedChoices);

            choices.addAll(newChoices);
            updateMenuOption(menu, choices);

            System.console().printf("%n");
            displayMenu(menu, promptMessage);
            input = System.console().readLine();
        }

        return choices;
    }

    private static Set<Integer> getRemovedChoices(Set<Integer> currentChoices, Set<Integer> newChoices) {
        Set<Integer> removedChoices = new HashSet<>();

        for (Integer choice : newChoices) {
            if (currentChoices.contains(choice)) {
                removedChoices.add(choice);
            }
        }

        return removedChoices;
    }

    private static void updateMenuOption(List<String> menu, Set<Integer> choices) {
        for (int i = 0; i < menu.size(); i++) {
            menu.set(i, menu.get(i).replace(" *", ""));
        }

        for (Integer choice : choices) {
            menu.set(choice - 1, menu.get(choice - 1).concat(" *"));
        }
    }

    public static boolean isValidIntegerChoices(Set<Integer> choices, int minChoice, int maxChoice) {
        for (Integer choice : choices) {
            if (choice < minChoice || choice > maxChoice) {
                return false;
            }
        }

        return !choices.isEmpty();
    }

    public static Set<Integer> parseRangeIntegerChoice(String input, int minChoice, int maxChoice) {
        Set<Integer> choices = new HashSet<>();
        Pattern enumeratePattern = Pattern.compile("^\\d[\\s\\d]*");
        Pattern rangePattern = Pattern.compile("^\\d-\\d");

        if (enumeratePattern.matcher(input).matches()) {
            choices = new HashSet<>(Arrays.asList(input.split(" ")).stream().map(Integer::parseInt).toList());
        } else if (rangePattern.matcher(input).matches()) {
            String[] minMax = input.split("-");
            int min = Integer.parseInt(minMax[0]);
            int max = Integer.parseInt(minMax[1]);

            if (min < max) {
                for (int i = min; i <= max; i++) {
                    choices.add(i);
                }
            }
        } else if (input.trim().isBlank()) {
            for (int i = minChoice; i <= maxChoice; i++) {
                choices.add(i);
            }
        }

        return isValidIntegerChoices(choices, minChoice, maxChoice) ? choices : new HashSet<>();
    }
}
