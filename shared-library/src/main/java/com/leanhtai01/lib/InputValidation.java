package com.leanhtai01.lib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class InputValidation {
    public static final String CHECK_MARK = "[\u2713]";
    public static final String PARTIALLY_CHECK_MARK = "[-]";

    private InputValidation() {
    }

    public static boolean isAnswerYes(String answer) {
        Pattern pattern = Pattern.compile("y|yes", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(answer).matches() || answer.isBlank();
    }

    public static void displayMenu(List<String> menu, String promptMessage) {
        for (int i = 0; i < menu.size(); i++) {
            System.console().printf("%d. %s%n", i, menu.get(i));
        }
        System.console().printf(promptMessage);
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
            unmarkInstall(menu, i);
        }

        for (Integer choice : choices) {
            markInstall(menu, choice, CHECK_MARK);
        }
    }

    public static void markInstall(List<String> menu, int index, String mark) {
        unmarkInstall(menu, index);
        menu.set(index, menu.get(index).concat(" %s".formatted(mark)));
    }

    public static void unmarkInstall(List<String> menu, int index) {
        menu.set(index, menu.get(index).replaceAll(" \\[.\\]", ""));
    }

    public static boolean isValidIntegerChoices(Set<Integer> choices, int minChoice, int maxChoice) {
        for (Integer choice : choices) {
            if (!isValidIntegerChoice(choice, minChoice, maxChoice)) {
                return false;
            }
        }

        return !choices.isEmpty();
    }

    public static boolean isValidIntegerChoice(int choice, int minChoice, int maxChoice) {
        return choice >= minChoice && choice <= maxChoice;
    }

    public static Set<Integer> getAllIntegerChoices(int minChoice, int maxChoice) {
        Set<Integer> choices = new HashSet<>();

        if (minChoice < maxChoice) {
            for (int i = minChoice; i <= maxChoice; i++) {
                choices.add(i);
            }
        }

        return choices;
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
