package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class OfficeInstall extends SoftwareInstall {
    public OfficeInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("libreoffice-fresh", "calibre", "kchmviewer", "foliate", "okular", "kolourpaint",
                "teams-for-linux"), userAccount, chrootDir);

        return 0;
    }
}
