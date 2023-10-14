package com.leanhtai01.archinstall.osinstall.tool;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChroot;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunSudo;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class DiskImageTool implements Installable {
    private String chrootDir;
    private UserAccount userAccount;

    public DiskImageTool(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("cdrtools", "cdemu-client", "vhba-module-dkms", "gcdemu"), userAccount, chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        List<String> loadDriversCommand = List.of("modprobe", "-a", "sg", "sr_mod", "vhba");

        runVerbose(chrootDir != null
                ? getCommandRunChroot(loadDriversCommand, chrootDir)
                : getCommandRunSudo(loadDriversCommand));

        return 0;
    }
}
