package com.leanhtai01.archinstall.osinstall.virtualmachine;

import static com.leanhtai01.archinstall.util.ConfigUtil.addUserToGroup;
import static com.leanhtai01.archinstall.util.ConfigUtil.backupFile;
import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.PackageUtil.installPackageFromFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class KVMInstall extends SoftwareInstall {
    public KVMInstall() {
        super(null, null);
    }

    public KVMInstall(String chrootDir) {
        super(chrootDir, null);
    }

    public KVMInstall(UserAccount userAccount) {
        super(null, userAccount);
    }

    public KVMInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPackageFromFile("packages-info/kvm.txt", chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        enableService("libvirtd", chrootDir);

        String libvirtdConfigPath = getPathPrefix() + "/etc/libvirt/libvirtd.conf";
        backupFile(libvirtdConfigPath);

        List<String> lines = Files.readAllLines(Paths.get(libvirtdConfigPath));
        int lineNumber = lines.indexOf("#unix_sock_group = \"libvirt\"");
        lines.set(lineNumber, lines.get(lineNumber).replace("#", ""));

        lineNumber = lines.indexOf("#unix_sock_rw_perms = \"0770\"");
        lines.set(lineNumber, lines.get(lineNumber).replace("#", ""));

        try (var writer = new PrintWriter(libvirtdConfigPath)) {
            for (String line : lines) {
                writer.println(line);
            }
        }

        if (userAccount != null) {
            addUserToGroup(userAccount.getUsername(), "libvirt", chrootDir);
            addUserToGroup(userAccount.getUsername(), "kvm", chrootDir);
        }

        return 0;
    }
}