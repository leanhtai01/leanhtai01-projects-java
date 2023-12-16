package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunSudo;
import static com.leanhtai01.archinstall.util.ShellUtil.runGetOutput;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class MakeRetailWindowsISO implements Runnable {
    @Override
    public void run() {
        try {
            System.console().printf("Enter path to Windows ISO: ");
            String pathToWindowsISO = System.console().readLine();

            if (!Files.exists(Paths.get(pathToWindowsISO)) || Files.isDirectory(Paths.get(pathToWindowsISO))) {
                System.console().printf("File not found!\n");
                return;
            }

            // get new ISO information
            String newPathName = FilenameUtils.removeExtension(pathToWindowsISO) + "-modified.iso";
            String newVolumeName = runGetOutput(List.of("bash", "-c",
                    "iso-info %s |".formatted(pathToWindowsISO)
                            + " grep -i '^Volume[ ]*:' |"
                            + " cut -d':' -f2 |"
                            + " sed 's/^ //g'"));

            // create new ISO
            runVerbose(getCommandRunSudo(List.of("mount", pathToWindowsISO, "/mnt", "-o", "loop")));
            Files.createDirectories(Paths.get("/tmp/modified/sources"));

            try (var writer = new PrintWriter("/tmp/modified/sources/ei.cfg")) {
                writer.print("[Channel]\r\nRetail\r\n");
            }

            runVerbose(List.of("bash", "-c", "mkisofs"
                    + " -iso-level 4"
                    + " -l"
                    + " -R"
                    + " -UDF"
                    + " -D"
                    + " -b boot/etfsboot.com"
                    + " -no-emul-boot"
                    + " -boot-load-size 8"
                    + " -hide boot.catalog"
                    + " -eltorito-alt-boot"
                    + " -eltorito-platform efi"
                    + " -no-emul-boot"
                    + " -b efi/microsoft/boot/efisys.bin"
                    + " -V %s".formatted(newVolumeName)
                    + " -o %s".formatted(newPathName)
                    + " /mnt"
                    + " /tmp/modified"));
            runVerbose(getCommandRunSudo(List.of("umount", "/mnt")));
            System.console().printf("Successfully making custom Windows iso!\n");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
