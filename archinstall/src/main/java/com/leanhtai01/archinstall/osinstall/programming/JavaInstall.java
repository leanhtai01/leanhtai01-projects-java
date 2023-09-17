package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;

public class JavaInstall extends SoftwareInstall {
    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("jdk-openjdk", "openjdk-doc", "openjdk-src", "java-openjfx", "java-openjfx-doc",
                "java-openjfx-src", "jdk17-openjdk", "java17-openjfx", "jdk11-openjdk", "java11-openjfx",
                "jdk8-openjdk", "maven", "gradle", "gradle-doc"), chrootDir);

        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Unimplemented method 'config'");
    }
}
