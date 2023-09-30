package com.leanhtai01.archinstall.menu;

public class SingleChoiceMenu extends Menu {
    @Override
    public String getPromptMessage() {
        return "==> ";
    }

    @Override
    public void selectOption() {
        displayMenu();
        String input = System.console().readLine();
        while (!input.trim().equals(String.valueOf(EXIT))) {
            try {
                int choice = Integer.parseInt(input);

                if (isValidChoice(choice)) {
                    clearAll();
                    options.get(choice).setMarked(true);
                } else {
                    System.console().printf("Invalid choice. Choice must be in range [%d, %d]%n", MIN_CHOICE, getMaxChoice());
                }
            } catch (NumberFormatException e) {
                System.console().printf("Invalid input format!%n");
            }

            System.console().printf("%n");
            displayMenu();
            input = System.console().readLine();
        }
    }

    private boolean isValidChoice(int choice) {
        return choice >= MIN_CHOICE && choice <= getMaxChoice();
    }
}
