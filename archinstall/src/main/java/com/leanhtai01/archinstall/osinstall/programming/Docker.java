package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.ConfigUtil.addUserToGroup;
import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.ConfigUtil.startService;
import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class Docker implements Installable {
    private final String chrootDir;
    private final UserAccount userAccount;

    public Docker(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("docker", "docker-compose"), chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        enableService("docker.service", chrootDir);

        if (chrootDir == null) {
            startService("docker.service", chrootDir);
        }

        if (userAccount != null) {
            addUserToGroup(userAccount.getUsername(), "docker", chrootDir);
        }

        return 0;
    }
}
