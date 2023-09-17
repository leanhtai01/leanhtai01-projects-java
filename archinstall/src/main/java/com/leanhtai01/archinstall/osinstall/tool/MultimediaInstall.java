package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class MultimediaInstall extends SoftwareInstall {
    public MultimediaInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("vlc", "gst-libav", "gst-plugins-good", "gst-plugins-ugly", "gst-plugins-bad", "obs-studio",
                "inkscape", "gimp", "kdenlive", "frei0r-plugins"), userAccount, chrootDir);

        return 0;
    }
}
