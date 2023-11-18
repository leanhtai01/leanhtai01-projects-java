package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class CoreTool implements Installable {
    private final String chrootDir;
    private final UserAccount userAccount;

    public CoreTool(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("keepassxc", "expect", "pacman-contrib", "dosfstools", "p7zip", "unarchiver",
                "bash-completion", "flatpak", "tree", "archiso", "rclone", "rsync", "lm_sensors",
                "ntfs-3g", "gparted", "exfatprogs", "pdftk", "youtube-dl", "ufw", "ufw-extras", "filezilla",
                "texlive-most", "krusader", "gptfdisk", "ventoy-bin"), userAccount, chrootDir);

        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        enableService("ufw", chrootDir);
        return 0;
    }
}
