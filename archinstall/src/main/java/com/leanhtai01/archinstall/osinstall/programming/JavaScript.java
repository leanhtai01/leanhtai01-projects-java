package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;
import static com.leanhtai01.archinstall.util.ShellUtil.runAppendOutputToFile;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class JavaScript implements Installable {
    private final String chrootDir;
    private final UserAccount userAccount;

    public JavaScript(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("eslint", "prettier", "nvm"), userAccount, chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        String bashrcPath = chrootDir + "/home/%s/.bashrc".formatted(userAccount.getUsername());

        runAppendOutputToFile(List.of("echo", "\n# nvm configuration\n"), bashrcPath);
        runAppendOutputToFile(List.of("echo", "source /usr/share/nvm/init-nvm.sh"), bashrcPath);

        return 0;
    }
}
