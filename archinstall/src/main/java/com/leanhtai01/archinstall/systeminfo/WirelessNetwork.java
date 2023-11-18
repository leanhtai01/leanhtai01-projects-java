package com.leanhtai01.archinstall.systeminfo;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.util.ShellUtil;

public class WirelessNetwork {
    private final String ssid;
    private final String password;
    private final String device;
    private final boolean isHidden;

    public WirelessNetwork(String ssid, String password, String device, boolean isHidden) {
        this.ssid = ssid;
        this.password = password;
        this.device = device;
        this.isHidden = isHidden;
    }

    public void connect() throws InterruptedException, IOException {
        ShellUtil.runVerbose(List.of("iwctl", "--passphrase=%s".formatted(password), "station", device,
                isHidden ? "connect-hidden" : "connect", ssid));
    }
}
