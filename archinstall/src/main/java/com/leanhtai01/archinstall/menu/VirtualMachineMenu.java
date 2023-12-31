package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.virtualmachine.KVM;
import com.leanhtai01.archinstall.osinstall.virtualmachine.VirtualBox;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class VirtualMachineMenu extends MultiChoiceMenu {
    public VirtualMachineMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new Option("KVM", new KVM(chrootDir, userAccount), false));
        addOption(new Option("VirtualBox", new VirtualBox(chrootDir, userAccount), false));
    }
}
