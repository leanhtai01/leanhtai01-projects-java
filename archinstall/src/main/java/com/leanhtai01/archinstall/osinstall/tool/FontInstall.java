package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;

public class FontInstall extends SoftwareInstall {
    public FontInstall(String chrootDir) {
        super(chrootDir, null);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("ttf-dejavu", "ttf-liberation", "noto-fonts-emoji", "ttf-cascadia-code",
                "ttf-fira-code", "ttf-roboto-mono", "ttf-hack"), chrootDir);

        return 0;
    }
}
