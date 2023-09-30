package com.leanhtai01.archinstall;

import com.leanhtai01.archinstall.menu.mainmenu.MainMenu;

public class Main {
    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.selectOption();
        mainMenu.doAction();
    }
}
