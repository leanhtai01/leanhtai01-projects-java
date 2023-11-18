package com.leanhtai01.archinstall.menu.mainmenu;

import com.leanhtai01.archinstall.osinstall.virtualmachine.VirtualBox;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

import java.io.IOException;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;

public class InstallVirtualBox implements Runnable {
    @Override
    public void run() {
        System.console().printf("Username: ");
        final String username = System.console().readLine();
        UserAccount userAccount = new UserAccount(null, username, null);
        VirtualBox virtualBox = new VirtualBox(null, userAccount);

        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            try {
                virtualBox.install();
                virtualBox.config();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
