package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.virtualmachine.KVM;
import com.leanhtai01.archinstall.osinstall.virtualmachine.VirtualBox;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class VirtualMachineInstallMenu extends InstallMenu {
    public VirtualMachineInstallMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new InstallOption("KVM", new KVM(chrootDir, userAccount), false));
        addOption(new InstallOption("VirtualBox", new VirtualBox(chrootDir, userAccount), false));
    }
}
