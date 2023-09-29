package com.leanhtai01.archinstall.osinstall.programming;

import static com.leanhtai01.archinstall.util.PackageUtil.installMainReposPkgs;

import java.io.IOException;
import java.util.List;

import com.leanhtai01.archinstall.osinstall.Installable;

public class PythonInstall implements Installable {
    private String chrootDir;

    public PythonInstall(String chrootDir) {
        this.chrootDir = chrootDir;
    }

    @Override
    public int install() throws InterruptedException, IOException {
        installMainReposPkgs(List.of("python", "jupyterlab", "python-nltk", "python-pandas", "python-pip",
                "python-numpy", "python-scikit-learn", "tk", "python-matplotlib", "python-docs", "autopep8",
                "python-requests", "python-beautifulsoup4", "python-pygame", "python-networkx"), chrootDir);

        return 0;
    }
}
