package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.ConfigUtil.backupFile;
import static com.leanhtai01.archinstall.util.ConfigUtil.disableService;
import static com.leanhtai01.archinstall.util.ConfigUtil.restartService;
import static com.leanhtai01.archinstall.util.ConfigUtil.stopService;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;

public class DisableDNSCryptProxy implements Runnable {
    @Override
    public void run() {
        try {
            String resolvConfigPath = "/etc/resolv.conf";

            stopService("dnscrypt-proxy.service", null);
            disableService("dnscrypt-proxy.service", null);

            runVerbose(List.of("chattr", "-i", resolvConfigPath));
            backupFile(resolvConfigPath + ".bak", resolvConfigPath);

            restartService("NetworkManager", null);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
