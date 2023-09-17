package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class CAndCPPInstall extends SoftwareInstall {
    public CAndCPPInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("clang", "llvm", "lldb", "cmake", "qt6-base", "qt6-doc", "qtcreator", "kdevelop"),
                userAccount, chrootDir);

        return 0;
    }
}
