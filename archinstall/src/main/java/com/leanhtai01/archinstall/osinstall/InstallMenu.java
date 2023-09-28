package com.leanhtai01.archinstall.osinstall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.leanhtai01.archinstall.systeminfo.UserAccount;
import com.leanhtai01.lib.InputValidation;

public abstract class InstallMenu {
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
        return 1;
    }

    public int getMaxChoice() {
        return menu.size();
    }

    public String getPromptMessage() {
        return "==> Enter your choice (e.g. '1', '1 2 3' or '1-3'), -1 to quit%n==> ";
    }

    public Set<Integer> getChoices() {
        return choices;
    }

    public Set<Integer> selectOptions() {
        InputValidation.chooseRangeIntegerOption(menu, getPromptMessage(),
                choices, getMinChoice(), getMaxChoice(), getExitOption());

        return choices;
    }

    public void setChoices(Set<Integer> choices) {
        if (InputValidation.isValidIntegerChoices(choices, getMinChoice(), getMaxChoice())) {
            for (int i = 0; i < menu.size(); i++) {
                menu.set(i, menu.get(i).replace(" *", ""));
            }

            for (Integer choice : choices) {
                menu.set(choice - 1, menu.get(choice - 1).concat(" *"));
            }

            this.choices = new HashSet<>(choices);
        }
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

    public abstract void install() throws IOException, InterruptedException;
}
