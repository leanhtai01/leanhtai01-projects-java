package com.leanhtai01.archinstall.systeminfo;

import java.io.IOException;

public class WirelessNetwork {
    private String ssid;
    private String password;
    private String device;
    private boolean isHidden;

    public WirelessNetwork(String ssid, String password, String device, boolean isHidden) {
        this.ssid = ssid;
        this.password = password;
        this.device = device;
        this.isHidden = isHidden;
    }

    public void connect() throws InterruptedException, IOException {
        new ProcessBuilder("iwctl", "--passphrase=%s".formatted(password), "station", device,
                isHidden ? "connect-hidden" : "connect", ssid).inheritIO().start().waitFor();
    }
}
