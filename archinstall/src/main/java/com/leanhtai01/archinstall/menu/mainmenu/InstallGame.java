package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgsWithOptionalDeps;

import java.io.IOException;
import java.util.List;

public class InstallGame implements Runnable {
    @Override
    public void run() {
        try {
            installMainReposPkgsWithOptionalDeps(List.of("lutris", "wine"), null);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
