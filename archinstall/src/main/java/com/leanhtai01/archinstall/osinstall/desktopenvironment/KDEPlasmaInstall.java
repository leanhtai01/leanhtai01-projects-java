package com.leanhtai01.archinstall.osinstall.desktopenvironment;

import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class KDEPlasmaInstall extends SoftwareInstall {
    public KDEPlasmaInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("xorg-server", "plasma-desktop", "plasma-wayland-session", "ark", "dolphin",
                "dolphin-plugins", "kate", "konsole", "kdegraphics-thumbnailers", "ffmpegthumbs", "spectacle",
                "gwenview", "bluedevil", "khotkeys", "kinfocenter", "kscreen", "plasma-firewall", "plasma-nm",
                "plasma-pa", "plasma-systemmonitor", "powerdevil", "sddm-kcm", "okular", "kcalc", "yakuake", "cryfs",
                "plasma-vault", "discover", "breeze-gtk", "kde-gtk-config", "gnome-keyring", "krusader",
                "kwalletmanager", "krename", "khelpcenter"), chrootDir);

        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        enableService("sddm", chrootDir);
        return 0;
    }
}
