package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class Office implements Installable {
    private final String chrootDir;
    private final UserAccount userAccount;

    public Office(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("libreoffice-fresh", "calibre", "kchmviewer", "foliate", "okular", "kolourpaint",
                "teams-for-linux", "telegram-desktop"), userAccount, chrootDir);

        return 0;
    }
}
