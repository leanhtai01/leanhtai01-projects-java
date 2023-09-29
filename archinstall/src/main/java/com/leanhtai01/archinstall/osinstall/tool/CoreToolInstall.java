package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class CoreToolInstall implements Installable {
    private String chrootDir;
    private UserAccount userAccount;

    public CoreToolInstall(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("keepassxc", "expect", "pacman-contrib", "dosfstools", "p7zip", "unarchiver",
                "bash-completion", "flatpak", "tree", "archiso", "rclone", "rsync", "transmission-gtk", "lm_sensors",
                "ntfs-3g", "gparted", "exfatprogs", "pdftk", "youtube-dl", "ufw", "ufw-extras", "filezilla",
                "texlive-most", "krusader", "ibus-bamboo", "gptfdisk"), userAccount, chrootDir);

        return 0;
    }
}
