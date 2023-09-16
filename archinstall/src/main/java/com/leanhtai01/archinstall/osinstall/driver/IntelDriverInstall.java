package com.leanhtai01.archinstall.osinstall.driver;

import static com.leanhtai01.archinstall.util.PackageUtil.installPackages;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;

public class IntelDriverInstall extends SoftwareInstall {
    public IntelDriverInstall() {
        super();
    }

    public IntelDriverInstall(String chrootDir) {
        super(chrootDir, null);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPackages(List.of("mesa", "lib32-mesa", "ocl-icd", "lib32-ocl-icd", "intel-compute-runtime"), chrootDir);
        installVAAPI();
        installVAAPI();
        return 0;
    }

    @Override
    public int config() throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    public void installVulkan() throws InterruptedException, IOException {
        installPackages(List.of("vulkan-intel", "lib32-vulkan-intel",
                "vulkan-icd-loader", "lib32-vulkan-icd-loader"),
                chrootDir);
    }

    public void installVAAPI() throws InterruptedException, IOException {
        installPackages(List.of("intel-media-driver", "libva-utils"), chrootDir);
    }
}
