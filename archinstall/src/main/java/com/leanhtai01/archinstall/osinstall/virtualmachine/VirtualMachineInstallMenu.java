package com.leanhtai01.archinstall.osinstall.virtualmachine;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.InstallMenu;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class VirtualMachineInstallMenu extends InstallMenu {
    public VirtualMachineInstallMenu(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
        menu.add("Install KVM");
        menu.add("Install VirtualBox");
    }

    @Override
    public void install() throws IOException, InterruptedException {
        for (Integer choice : choices) {
            switch (choice) {
                case 1 -> {
                    KVMInstall kvmInstall = new KVMInstall(chrootDir, userAccount);
                    kvmInstall.install();
                    kvmInstall.config();
                }
                case 2 -> {
                    VirtualBoxInstall virtualBoxInstall = new VirtualBoxInstall(chrootDir, userAccount);
                    virtualBoxInstall.install();
                    virtualBoxInstall.config();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid choice");
                }
            }
        }
    }
}
