package com.leanhtai01.archinstall.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MultiChoiceMenu {
    private static final int EXIT = -1;
    private static final int MIN_CHOICE = 0;

    private int optionCount;
    private List<MultiChoiceOption> multiChoiceOptions;

    public MultiChoiceMenu() {
        optionCount = MIN_CHOICE;
        multiChoiceOptions = new ArrayList<>();
    }

    public void addOption(MultiChoiceOption option) {
        multiChoiceOptions.add(option);
        option.setOptionNumber(optionCount);
        optionCount++;
    }

    public void displayMenu() {
        for (MultiChoiceOption option : multiChoiceOptions) {
            System.console().printf("%s\n", option);
        }

        System.console().printf("%s%n%s",
                "==> Enter your choice (e.g. '0', '0 1 2' or '0-2'), -1 to quit",
                "==> ");
    }

    public void doAction() {
        for (MultiChoiceOption option : multiChoiceOptions) {
            option.doAction();
        }
    }

    public String getActionSummary() {
        List<String> markedOptions = new ArrayList<>();

        for (MultiChoiceOption option : multiChoiceOptions) {
            if (option.isMarked()) {
                markedOptions.add(option.getDescription());
            }
        }

        return getClass().getSimpleName() + "=" + markedOptions;
    }

    public void setOptions(Set<Integer> choices) {
        if (isValidChoices(choices)) {
            clearAll();
            for (Integer choice : choices) {
                multiChoiceOptions.get(choice).setMarked(true);
            }
        }
    }

    public void selectAll() {
        for (MultiChoiceOption option : multiChoiceOptions) {
            option.setMarked(true);
        }
    }

    public void clearAll() {
        for (MultiChoiceOption option : multiChoiceOptions) {
            option.setMarked(false);
        }
    }

    public void selectOptions() {
        displayMenu();
        String input = System.console().readLine();
        while (!input.trim().equals(String.valueOf(EXIT))) {
            Set<Integer> choices = parseChoice(input);
            for (Integer choice : choices) {
                multiChoiceOptions.get(choice).toggleMark();
            }

            System.console().printf("%n");
            displayMenu();
            input = System.console().readLine();
        }
    }

    public Set<Integer> parseChoice(String input) {
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
            for (int i = MIN_CHOICE; i <= getMaxChoice(); i++) {
                choices.add(i);
            }
        } else {
            System.console().printf("Invalid input format!%n");
        }

        if (!isValidChoices(choices)) {
            System.console().printf("Invalid choices. Choice must be in range [%d, %d]%n", MIN_CHOICE, getMaxChoice());
            choices.clear();
        }

        return choices;
    }

    private int getMaxChoice() {
        return multiChoiceOptions.size() - 1;
    }

    private boolean isValidChoices(Set<Integer> choices) {
        for (Integer choice : choices) {
            if (choice < MIN_CHOICE || choice > getMaxChoice()) {
                return false;
            }
        }

        return true;
    }
}
