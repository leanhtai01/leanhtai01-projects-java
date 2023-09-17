package com.leanhtai01.archinstall.osinstall.driver;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;
import static com.leanhtai01.archinstall.util.ShellUtil.getCommandRunChrootAsUser;
import static com.leanhtai01.archinstall.util.ShellUtil.runVerbose;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class PipeWireInstall extends SoftwareInstall {
    public PipeWireInstall() {
        super(null, null);
    }

    public PipeWireInstall(String chrootDir) {
        super(chrootDir, null);
    }

    public PipeWireInstall(UserAccount userAccount) {
        super(null, userAccount);
    }

    public PipeWireInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("pipewire", "pipewire-pulse", "pipewire-alsa", "alsa-utils",
                "gst-plugin-pipewire", "lib32-pipewire", "wireplumber"), chrootDir);

        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        if (userAccount != null) {
            List<String> mkdirCmd = List.of("mkdir", "-p",
                    "/home/%s/.config/pipewire".formatted(userAccount.getUsername()));
            runVerbose(chrootDir != null
                    ? getCommandRunChrootAsUser(mkdirCmd, userAccount.getUsername(), chrootDir)
                    : mkdirCmd);

            List<String> cpCfgCmd = List.of("cp", "-r", "/usr/share/pipewire",
                    "/home/%s/.config/".formatted(userAccount.getUsername()));
            runVerbose(chrootDir != null
                    ? getCommandRunChrootAsUser(cpCfgCmd, userAccount.getUsername(), chrootDir)
                    : cpCfgCmd);

            List<String> cfgHighQuality = List.of("bash", "-c",
                    "sed -i '/resample.quality/s/#//; /resample.quality/s/4/10/'" + " "
                            + "/home/%s/.config/pipewire/{client.conf,pipewire-pulse.conf}"
                                    .formatted(userAccount.getUsername()));
            runVerbose(chrootDir != null
                    ? getCommandRunChrootAsUser(cfgHighQuality, userAccount.getUsername(), chrootDir)
                    : cfgHighQuality);
        }

        return 0;
    }
}
