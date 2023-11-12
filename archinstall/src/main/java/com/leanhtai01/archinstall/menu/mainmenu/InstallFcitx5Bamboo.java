package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.KDEPlasma;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class InstallFcitx5Bamboo implements Runnable {
    @Override
    public void run() {
        System.console().printf("Username: ");
        final String username = System.console().readLine();
        UserAccount userAccount = new UserAccount(null, username, null);
        KDEPlasma kdePlasma = new KDEPlasma(null, userAccount);

        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            try {
                kdePlasma.installFcitx5Bamboo();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
