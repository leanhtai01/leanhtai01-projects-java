package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.ConfigUtil.backupFile;
import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.ConfigUtil.restartService;
import static com.leanhtai01.archinstall.util.ConfigUtil.startService;
import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class EnableDNSCryptProxy implements Runnable {
    @Override
    public void run() {
        try {
            String resolvConfigPath = "/etc/resolv.conf";

            installMainReposPkgs(List.of("ddclient", "dnscrypt-proxy"), null);
            backupFile(resolvConfigPath, resolvConfigPath + ".bak");

            try (var writer = new PrintWriter(resolvConfigPath)) {
                writer.println("nameserver ::1");
                writer.println("nameserver 127.0.0.1");
                writer.println("options edns0");
            }

            runVerbose(List.of("chattr", "+i", resolvConfigPath));

            enableService("dnscrypt-proxy.service", null);
            startService("dnscrypt-proxy.service", null);
            restartService("NetworkManager", null);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
