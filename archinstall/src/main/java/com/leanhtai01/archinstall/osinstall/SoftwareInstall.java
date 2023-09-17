package com.leanhtai01.archinstall.osinstall;

import java.io.IOException;

import com.leanhtai01.archinstall.systeminfo.UserAccount;

public abstract class SoftwareInstall {
    protected final String chrootDir;
    protected final UserAccount userAccount;

    protected SoftwareInstall() {
        chrootDir = null;
        userAccount = null;
    }

    protected SoftwareInstall(String chrootDir, UserAccount userAccount) {
        this.chrootDir = chrootDir;
        this.userAccount = userAccount;
    }

    public String getChrootDir() {
        return chrootDir;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public int install() throws InterruptedException, IOException {
        return 0;
    }

    public int config() throws IOException, InterruptedException {
        return 0;
    }

    public String getPathPrefix() {
        return chrootDir != null ? chrootDir : "";
    }
}
