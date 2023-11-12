package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;

import java.io.IOException;

public class InstallFlatpakPackages implements Runnable {
    @Override
    public void run() {
        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            try {
                ConfigureSystem.installFlatpakPkgs();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
