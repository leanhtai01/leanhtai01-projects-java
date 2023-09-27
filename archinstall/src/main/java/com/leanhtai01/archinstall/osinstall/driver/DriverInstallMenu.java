package com.leanhtai01.archinstall.osinstall.driver;

import static com.leanhtai01.lib.InputValidation.displayMenu;
import static com.leanhtai01.lib.InputValidation.parseRangeIntegerChoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DriverInstallMenu {
    private static final int EXIT = -1;

    private final String chrootDir;
    private final UserAccount userAccount;

    private List<String> menu;
    private Set<Integer> choices;

    public DriverInstallMenu(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;

        menu = new ArrayList<>();
        menu.add("Install PipeWire");
        menu.add("Install Intel drivers");
        menu.add("Install TLP");

        choices = new HashSet<>();
    }

    public Set<Integer> getChoices() {
        final int minChoice = 1;
        final int maxChoice = menu.size();
        final String promptMessage = "==> Enter your choice (e.g. '1', '1 2 3' or '1-3'), -1 to quit%n==> ";

        displayMenu(menu, promptMessage);
        String input = System.console().readLine();
        while (!input.trim().equals(String.valueOf(EXIT))) {
            Set<Integer> newChoices = parseRangeIntegerChoice(input, minChoice, maxChoice);
            Set<Integer> removedChoices = getRemovedChoices(choices, newChoices);
            choices.removeAll(removedChoices);
            newChoices.removeAll(removedChoices);

            choices.addAll(newChoices);
            updateMenuOption(newChoices, removedChoices);

            System.console().printf("%n");
            displayMenu(menu, promptMessage);
            input = System.console().readLine();
        }

        choices.remove(EXIT);

        return choices;
    }

    private void updateMenuOption(Set<Integer> newChoices, Set<Integer> removedChoices) {
        for (Integer removed : removedChoices) {
            menu.set(removed - 1, menu.get(removed - 1).replace(" *", ""));
        }

        for (Integer choice : newChoices) {
            menu.set(choice - 1, menu.get(choice - 1).concat(" *"));
        }
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

    public String getInstallSummary() {
        List<String> installSummary = new ArrayList<>();
        Pattern pattern = Pattern.compile("^Install (.*) (\\*)");

        for (int i = 0; i < menu.size(); i++) {
            if (choices.contains(i + 1)) {
                Matcher matcher = pattern.matcher(menu.get(i));
                if (matcher.matches()) {
                    installSummary.add(matcher.group(1));
                }
            }
        }

        return this.getClass().getSimpleName() + "=" + installSummary;
    }

    public void install()
            throws IOException, InterruptedException {
        for (Integer choice : choices) {
            switch (choice) {
                case 1 -> {
                    PipeWireInstall pipeWireInstall = new PipeWireInstall(chrootDir, userAccount);
                    pipeWireInstall.install();
                    pipeWireInstall.config();
                }
                case 2 -> {
                    IntelDriverInstall intelDriverInstall = new IntelDriverInstall(chrootDir);
                    intelDriverInstall.install();
                    intelDriverInstall.config();
                }
                case 3 -> {
                    TLPInstall tlpInstall = new TLPInstall(chrootDir, userAccount);
                    tlpInstall.install();
                    tlpInstall.config();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid choice");
                }
            }
        }
    }

    public static void main(String[] args) {
        DriverInstallMenu driverInstallMenu = new DriverInstallMenu(null, null);
        driverInstallMenu.getChoices();
        System.console().printf("%s%n", driverInstallMenu.getInstallSummary());
    }
}
