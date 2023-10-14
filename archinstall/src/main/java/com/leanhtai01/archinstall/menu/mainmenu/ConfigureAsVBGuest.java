package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.ConfigUtil.addUserToGroup;
import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class ConfigureAsVBGuest implements Runnable {
    private UserAccount userAccount;
    private String chrootDir;

    public ConfigureAsVBGuest(UserAccount userAccount, String chrootDir) {
        this.userAccount = userAccount;
        this.chrootDir = chrootDir;
    }

    @Override
    public void run() {
        if (userAccount == null) {
            System.console().printf("Username: ");
            final String username = System.console().readLine();

            userAccount = new UserAccount(null, username, null);
        }

        try {
            installMainReposPkgs(List.of("virtualbox-guest-utils"), chrootDir);
            addUserToGroup(userAccount.getUsername(), "vboxsf", chrootDir);
            enableService("vboxservice.service", chrootDir);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
