package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class GTKProgrammingTool implements Installable {
    private String chrootDir;

    public GTKProgrammingTool(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("devhelp", "glade", "gnome-builder", "gnome-code-assistance", "gnome-devel-docs"),
                chrootDir);

        return 0;
    }
}
