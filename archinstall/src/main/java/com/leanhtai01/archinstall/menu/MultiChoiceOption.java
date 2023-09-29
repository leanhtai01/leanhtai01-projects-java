package com.leanhtai01.archinstall.menu;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.Installable;

public class MultiChoiceOption {
    private static final String CHECK_MARK = "[\u2713]";

    private int optionNumber;
    private String description;
    private boolean isMarked;
    private Installable installable;

    public MultiChoiceOption(String description, Installable installable, boolean isMarked) {
        this.description = description;
        this.installable = installable;
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

    public void install() throws IOException, InterruptedException {
        if (isMarked) {
            installable.install();
            installable.config();
        }
    }

    @Override
    public String toString() {
        return "%d. %s".formatted(optionNumber, description) + (isMarked ? " " + CHECK_MARK : "");
    }
}
