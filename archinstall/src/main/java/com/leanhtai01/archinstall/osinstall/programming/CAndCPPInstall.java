package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class CAndCPPInstall implements Installable {
    private String chrootDir;

    public CAndCPPInstall(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("clang", "llvm", "lldb", "cmake", "qt6-base", "qt6-doc", "qtcreator", "kdevelop"),
                chrootDir);

        return 0;
    }
}
