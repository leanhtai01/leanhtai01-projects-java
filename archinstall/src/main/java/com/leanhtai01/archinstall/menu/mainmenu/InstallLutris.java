package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;
import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgsWithOptionalDeps;

import java.io.IOException;
import java.util.List;

public class InstallLutris implements Runnable {
    @Override
    public void run() {
        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            try {
                installMainReposPkgsWithOptionalDeps(List.of("lutris", "wine"), null);
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
