package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class CoreToolInstall extends SoftwareInstall {
    public CoreToolInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
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
