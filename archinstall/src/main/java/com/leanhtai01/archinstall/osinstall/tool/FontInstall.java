package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class FontInstall implements Installable {
    private String chrootDir;

    public FontInstall(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("ttf-dejavu", "ttf-liberation", "noto-fonts-emoji", "ttf-cascadia-code",
                "ttf-fira-code", "ttf-roboto-mono", "ttf-hack"), chrootDir);

        return 0;
    }
}
