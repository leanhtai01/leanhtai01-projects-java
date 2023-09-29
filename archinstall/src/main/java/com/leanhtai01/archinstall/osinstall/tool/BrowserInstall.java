package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class BrowserInstall implements Installable {
    private String chrootDir;
    private UserAccount userAccount;

    public BrowserInstall(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("torbrowser-launcher", "firefox-developer-edition", "firefox", "google-chrome"),
                userAccount, chrootDir);

        return 0;
    }
}
