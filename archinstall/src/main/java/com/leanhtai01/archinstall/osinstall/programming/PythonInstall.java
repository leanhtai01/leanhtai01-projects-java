package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.SoftwareInstall;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class PythonInstall extends SoftwareInstall {
    public PythonInstall(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installPkgs(List.of("python", "jupyterlab", "python-nltk", "python-pandas", "python-pip", "python-numpy",
                "python-scikit-learn", "tk", "python-matplotlib", "python-docs", "autopep8", "python-requests",
                "python-beautifulsoup4", "python-pygame", "python-networkx"), userAccount, chrootDir);

        return 0;
    }
}
