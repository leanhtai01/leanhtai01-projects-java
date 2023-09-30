package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.DiskUtil.encryptDiskUsingLUKS;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.util.IOUtil;

public class EncryptDisk implements Runnable {
    @Override
    public void run() {
        try {
            runVerbose(List.of("lsblk"));
            System.console().printf("Enter disk name: ");
            String diskName = System.console().readLine();

            System.console().printf("Enter mapper name: ");
            String mapperName = System.console().readLine();

            String password = IOUtil.readPassword(
                    "Enter LUKS password: ",
                    "Re-enter LUKS password: ");

            System.console().printf(":: Proceed with encryption? [Y/n] ");
            String answer = System.console().readLine();
            if (IOUtil.isAnswerYes(answer)) {
                encryptDiskUsingLUKS(diskName, mapperName, password);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
