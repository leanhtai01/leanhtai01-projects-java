package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class BrowserInstall implements Installable {
    private String chrootDir;

    public BrowserInstall(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("torbrowser-launcher", "firefox-developer-edition", "firefox", "google-chrome"),
                chrootDir);

        return 0;
    }
}
