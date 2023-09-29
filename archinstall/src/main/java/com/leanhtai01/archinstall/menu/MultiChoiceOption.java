package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.Installable;

public class MultiChoiceOption {
    private static final String CHECK_MARK = "[\u2713]";

    private int optionNumber;
    private String description;
    private boolean isMarked;
    private Runnable runnable;

    public MultiChoiceOption(String description, Installable installable, boolean isMarked) {
        this.description = description;
        this.runnable = installable;
        this.isMarked = isMarked;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean isMarked) {
        this.isMarked = isMarked;
    }

    public void toggleMark() {
        isMarked = !isMarked;
    }

    public void setOptionNumber(int optionNumber) {
        this.optionNumber = optionNumber;
    }

    public void doAction() {
        if (isMarked) {
            runnable.run();
        }
    }

    @Override
    public String toString() {
        return "%d. %s".formatted(optionNumber, description) + (isMarked ? " " + CHECK_MARK : "");
    }
}
