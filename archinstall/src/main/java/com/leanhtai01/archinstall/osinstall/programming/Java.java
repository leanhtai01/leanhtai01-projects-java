package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class Java implements Installable {
    private final String chrootDir;
    private final UserAccount userAccount;

    public Java(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("jdk-openjdk", "openjdk-doc", "openjdk-src", "jdk11-openjdk", "maven", "gradle",
                "gradle-doc", "jetbrains-toolbox", "xorg-fonts-type1"), userAccount, chrootDir);

        return 0;
    }
}
