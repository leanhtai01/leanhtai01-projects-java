package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class MultimediaInstall implements Installable {
    private String chrootDir;

    public MultimediaInstall(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("vlc", "gst-libav", "gst-plugins-good", "gst-plugins-ugly", "gst-plugins-bad",
                "obs-studio", "inkscape", "gimp", "kdenlive", "frei0r-plugins"), chrootDir);

        return 0;
    }
}
