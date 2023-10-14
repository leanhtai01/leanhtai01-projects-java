package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;
import static com.leanhtai01.archinstall.util.PackageUtil.installFlatpakPackages;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.GNOME;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class ConfigureSystem implements Runnable {
    private UserAccount userAccount;

    private void getInfo() {
        System.console().printf("Username: ");
        final String username = System.console().readLine();

        userAccount = new UserAccount(null, username, null);
    }

    @Override
    public void run() {
        getInfo();

        if (isAnswerYes(getConfirmation(":: Proceed with configuration? [Y/n] "))) {
            try {
                configureGNOME();
                installFlatpakPkgs();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void configureGNOME() throws InterruptedException, IOException {
        GNOME gnomeInstall = new GNOME(null, userAccount);
        gnomeInstall.configureDesktopInterface();
        gnomeInstall.createCustomShortcut(gnomeInstall.readShortcutsFromFile("gnome-shortcuts.txt"));
        gnomeInstall.configureIbusBamboo();
        gnomeInstall.enableExtension("appindicatorsupport@rgcjonas.gmail.com");
    }

    public void installFlatpakPkgs() throws InterruptedException, IOException {
        installFlatpakPackages(List.of("org.goldendict.GoldenDict", "com.belmoussaoui.Authenticator"));
    }
}
