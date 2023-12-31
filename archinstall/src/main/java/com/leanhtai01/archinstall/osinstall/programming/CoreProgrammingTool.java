package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class CoreProgrammingTool implements Installable {
    private final String chrootDir;
    private final UserAccount userAccount;

    public CoreProgrammingTool(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("git", "github-cli", "kdiff3", "valgrind", "kruler", "sublime-merge",
                "visual-studio-code-bin", "postman-bin", "emacs-wayland"), userAccount, chrootDir);

        return 0;
    }
}
