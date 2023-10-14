package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChroot;
import static com.leanhtai01.archinstall.util.ShellUtil.runAppendOutputToFile;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class JavaScript implements Installable {
    private String chrootDir;
    private UserAccount userAccount;

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
        List<String> changeBashrcOwner = List.of(
                "chown", "%s:%s".formatted(userAccount.getUsername(), userAccount.getUsername()), bashrcPath);

        runAppendOutputToFile(List.of("echo", "source /usr/share/nvm/init-nvm.sh"), bashrcPath);
        runVerbose(chrootDir != null ? getCommandRunChroot(changeBashrcOwner, chrootDir) : changeBashrcOwner);

        return 0;
    }
}
