package com.leanhtai01.archinstall.osinstall.tool;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.InstallMenu;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class ToolInstallMenu extends InstallMenu {
    public ToolInstallMenu(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
        menu.add("Install Core Tools");
        menu.add("Install Fonts");
        menu.add("Install Browsers");
        menu.add("Install Disk Image Tools");
        menu.add("Install Multimedia");
        menu.add("Install Network");
        menu.add("Install Office");
        menu.add("Install Remote desktop");
    }

    @Override
    public void install() throws IOException, InterruptedException {
        for (Integer choice : choices) {
            switch (choice) {
                case 1 -> {
                    CoreToolInstall coreToolInstall = new CoreToolInstall(chrootDir, userAccount);
                    coreToolInstall.install();
                    coreToolInstall.config();
                }
                case 2 -> {
                    FontInstall fontInstall = new FontInstall(chrootDir);
                    fontInstall.install();
                    fontInstall.config();
                }
                case 3 -> {
                    BrowserInstall browserInstall = new BrowserInstall(chrootDir, userAccount);
                    browserInstall.install();
                    browserInstall.config();
                }
                case 4 -> {
                    DiskImageToolInstall diskImageToolInstall = new DiskImageToolInstall(chrootDir, userAccount);
                    diskImageToolInstall.install();
                    diskImageToolInstall.config();
                }
                case 5 -> {
                    MultimediaInstall multimediaInstall = new MultimediaInstall(chrootDir, userAccount);
                    multimediaInstall.install();
                    multimediaInstall.config();
                }
                case 6 -> {
                    NetworkToolInstall networkToolInstall = new NetworkToolInstall(chrootDir, userAccount);
                    networkToolInstall.install();
                    networkToolInstall.config();
                }
                case 7 -> {
                    OfficeInstall officeInstall = new OfficeInstall(chrootDir, userAccount);
                    officeInstall.install();
                    officeInstall.config();
                }
                case 8 -> {
                    RemoteDesktopInstall remoteDesktopInstall = new RemoteDesktopInstall(chrootDir, userAccount);
                    remoteDesktopInstall.install();
                    remoteDesktopInstall.config();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid choice");
                }
            }
        }
    }
}
