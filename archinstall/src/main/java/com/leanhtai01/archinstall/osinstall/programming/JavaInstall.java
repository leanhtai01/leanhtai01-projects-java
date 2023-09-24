package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class JavaInstall extends SoftwareInstall {
    public JavaInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("jdk-openjdk", "openjdk-doc", "openjdk-src", "java-openjfx", "java-openjfx-doc",
                "java-openjfx-src", "jdk17-openjdk", "java17-openjfx", "jdk11-openjdk", "java11-openjfx",
                "jdk8-openjdk", "maven", "gradle", "gradle-doc", "jetbrains-toolbox", "xorg-fonts-type1"),
                userAccount, chrootDir);

        return 0;
    }
}
