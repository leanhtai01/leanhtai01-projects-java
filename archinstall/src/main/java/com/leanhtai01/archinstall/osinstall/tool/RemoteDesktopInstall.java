package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class RemoteDesktopInstall implements Installable {
    private String chrootDir;

    public RemoteDesktopInstall(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("remmina", "freerdp", "spice-gtk", "libvncserver"), chrootDir);
        return 0;
    }
}
