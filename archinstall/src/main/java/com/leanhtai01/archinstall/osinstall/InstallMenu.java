package com.leanhtai01.archinstall.osinstall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public abstract class InstallMenu {
    public static final String CHECK_MARK = "[\u2713]";

    protected final String chrootDir;
    protected final UserAccount userAccount;
    protected List<String> menu;
    protected Set<Integer> choices;

    protected InstallMenu(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
        menu = new ArrayList<>();
        choices = new HashSet<>();
    }

    public int getExitOption() {
        return -1;
    }

    public int getMinChoice() {
        return 0;
    }

    public int getMaxChoice() {
        return menu.size() - 1;
    }

    public String getPromptMessage() {
        return "==> Enter your choice (e.g. '0', '0 1 2' or '0-2'), -1 to quit%n==> ";
    }

    public Set<Integer> getChoices() {
        return choices;
    }

    public void setChoices(Set<Integer> choices) {
        if (isValidChoices(choices, getMinChoice(), getMaxChoice())) {
            for (int i = 0; i < menu.size(); i++) {
                unmarkInstall(menu, i);
            }

            for (Integer choice : choices) {
                markInstall(menu, choice, CHECK_MARK);
            }

            this.choices = new HashSet<>(choices);
        }
    }

    private void displayMenu(List<String> menu, String promptMessage) {
        for (int i = 0; i < menu.size(); i++) {
            System.console().printf("%d. %s%n", i, menu.get(i));
        }
        System.console().printf(promptMessage);
    }

    private Set<Integer> getRemovedChoices(Set<Integer> currentChoices, Set<Integer> newChoices) {
        Set<Integer> removedChoices = new HashSet<>();

        for (Integer choice : newChoices) {
            if (currentChoices.contains(choice)) {
                removedChoices.add(choice);
            }
        }

        return removedChoices;
    }

    public void updateMenu(List<String> menu, Set<Integer> choices) {
        for (int i = 0; i < menu.size(); i++) {
            unmarkInstall(menu, i);
        }

        for (Integer choice : choices) {
            markInstall(menu, choice, CHECK_MARK);
        }
    }

    public void markInstall(List<String> menu, int index, String mark) {
        unmarkInstall(menu, index);
        menu.set(index, menu.get(index).concat(" %s".formatted(mark)));
    }

    public void unmarkInstall(List<String> menu, int index) {
        menu.set(index, menu.get(index).replaceAll(" \\[.\\]", ""));
    }

    public static boolean isValidChoices(Set<Integer> choices, int minChoice, int maxChoice) {
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

        return isValidChoices(choices, minChoice, maxChoice) ? choices : new HashSet<>();
    }

    public Set<Integer> selectOptions() {
        displayMenu(menu, getPromptMessage());
        String input = System.console().readLine();
        while (!input.trim().equals(String.valueOf(getExitOption()))) {
            Set<Integer> newChoices = parseRangeIntegerChoice(input, getMinChoice(), getMaxChoice());
            Set<Integer> removedChoices = getRemovedChoices(choices, newChoices);
            choices.removeAll(removedChoices);
            newChoices.removeAll(removedChoices);

            choices.addAll(newChoices);
            updateMenu(menu, choices);

            System.console().printf("%n");
            displayMenu(menu, getPromptMessage());
            input = System.console().readLine();
        }

        return choices;
    }

    public String getInstallSummary() {
        List<String> installSummary = new ArrayList<>();
        Pattern pattern = Pattern.compile("^Install (.*) (.*)");

        for (int i = 0; i < menu.size(); i++) {
            if (choices.contains(i)) {
                Matcher matcher = pattern.matcher(menu.get(i));
                if (matcher.matches()) {
                    installSummary.add(matcher.group(1));
                }
            }
        }

        return this.getClass().getSimpleName() + "=" + installSummary;
    }

    public abstract void install() throws IOException, InterruptedException;
}
