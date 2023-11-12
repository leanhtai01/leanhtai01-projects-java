package com.leanhtai01.archinstall.menu.mainmenu;

import java.io.IOException;

public class InstallFlatpakPackages implements Runnable {
    @Override
    public void run() {
        try {
            ConfigureSystem.installFlatpakPkgs();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
