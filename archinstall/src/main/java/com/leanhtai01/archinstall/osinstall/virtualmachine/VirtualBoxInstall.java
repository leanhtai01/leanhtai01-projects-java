package com.leanhtai01.archinstall.osinstall.virtualmachine;

import static com.leanhtai01.archinstall.util.ConfigUtil.addUserToGroup;
import static com.leanhtai01.archinstall.util.PackageUtil.installAURPackages;
import static com.leanhtai01.archinstall.util.PackageUtil.installPackages;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class VirtualBoxInstall extends SoftwareInstall {
    public VirtualBoxInstall(UserAccount userAccount) {
        super(null, userAccount);
    }

    public VirtualBoxInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPackages(List.of("virtualbox", "virtualbox-guest-iso", "virtualbox-host-dkms"), chrootDir);
        installAURPackages(List.of("virtualbox-ext-oracle"), userAccount, chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        addUserToGroup(userAccount.getUsername(), "vboxusers", chrootDir);
        return 0;
    }

}
