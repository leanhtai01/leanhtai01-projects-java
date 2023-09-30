package com.leanhtai01.archinstall.util;

import static com.leanhtai01.archinstall.util.IOUtil.readPassword;
import static com.leanhtai01.archinstall.util.ShellUtil.runSilent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.leanhtai01.archinstall.systeminfo.WirelessNetwork;

public final class NetworkUtil {
    private NetworkUtil() {
    }

    public static void connectToWifi() throws InterruptedException, IOException {
        boolean isConnected = isConnectedToInternet();

        if (!isConnected) {
            System.console().printf("Connecting to Wifi...%n");

            do {
                System.console().printf("SSID: ");
                String ssid = System.console().readLine();

                String password = readPassword("Wifi password: ", "Re-enter Wifi password: ");

                WirelessNetwork network = new WirelessNetwork(ssid, password, "wlan0", true);
                network.connect();
                TimeUnit.SECONDS.sleep(5);

                isConnected = isConnectedToInternet();

                if (!isConnected) {
                    System.console().printf("Invalid SSID or password. Please try again!%n");
                }

            } while (!isConnected);
        }
    }

    public static boolean isConnectedToInternet() throws IOException, InterruptedException {
        return runSilent(List.of("ping", "-c", "3", "www.google.com")) == 0;
    }
}
