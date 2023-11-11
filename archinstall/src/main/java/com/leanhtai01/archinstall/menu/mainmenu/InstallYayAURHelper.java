package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;
import static com.leanhtai01.archinstall.util.PackageUtil.installYayAURHelper;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class InstallYayAURHelper implements Runnable {
    @Override
    public void run() {
        System.console().printf("Username: ");
        final String username = System.console().readLine();
        UserAccount userAccount = new UserAccount(null, username, null);

        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            try {
                installYayAURHelper(userAccount, null);
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
