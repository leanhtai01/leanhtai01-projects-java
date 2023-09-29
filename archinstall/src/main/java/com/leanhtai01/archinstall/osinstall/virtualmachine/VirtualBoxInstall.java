package com.leanhtai01.archinstall.osinstall.virtualmachine;

import static com.leanhtai01.archinstall.util.ConfigUtil.addUserToGroup;
import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class VirtualBoxInstall implements Installable {
    private String chrootDir;
    private UserAccount userAccount;

    public VirtualBoxInstall(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("virtualbox", "virtualbox-guest-iso", "virtualbox-host-dkms", "virtualbox-ext-oracle"),
                userAccount, chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        addUserToGroup(userAccount.getUsername(), "vboxusers", chrootDir);
        return 0;
    }

}
