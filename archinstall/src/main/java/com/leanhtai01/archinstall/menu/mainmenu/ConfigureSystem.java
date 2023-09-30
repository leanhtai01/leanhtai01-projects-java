package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.PackageUtil.installFlatpakPackages;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.GNOME;
import com.leanhtai01.archinstall.systeminfo.UserAccount;
import com.leanhtai01.archinstall.util.IOUtil;

public class ConfigureSystem implements Runnable {
    private UserAccount userAccount;

    private void getInfo() {
        System.console().printf("Username: ");
        final String username = System.console().readLine();

        final String userPassword = IOUtil.readPassword(
                "User's password: ",
                "Re-enter User's password: ");

        userAccount = new UserAccount(null, username, userPassword);
    }

    @Override
    public void run() {
        getInfo();

        System.console().printf(":: Proceed with configuration? [Y/n] ");
        String answer = System.console().readLine();
        if (IOUtil.isAnswerYes(answer)) {
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
