package com.leanhtai01.archinstall.menu.mainmenu;

import java.util.List;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;
import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

public class InstallValveSteam implements Runnable {
    @Override
    public void run() {
        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            try {
                installMainReposPkgs(List.of("steam", "steam-native-runtime"), null);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
