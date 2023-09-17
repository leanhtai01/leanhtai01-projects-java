package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class GTKProgrammingInstall extends SoftwareInstall {
    public GTKProgrammingInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("devhelp", "glade", "gnome-builder", "gnome-code-assistance", "gnome-devel-docs"),
                userAccount, chrootDir);

        return 0;
    }
}
