package com.leanhtai01.archinstall.osinstall.driver;

import static com.leanhtai01.archinstall.util.ConfigUtil.enableService;
import static com.leanhtai01.archinstall.util.ConfigUtil.startService;
import static com.leanhtai01.archinstall.util.PackageUtil.installAutoAnswerYes;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class TLP implements Installable {
    private String chrootDir;

    public TLP(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installAutoAnswerYes(List.of("tlp"), chrootDir);
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        enableService("tlp", chrootDir);

        if (chrootDir == null) {
            startService("tlp", chrootDir);
        }

        return 0;
    }
}
