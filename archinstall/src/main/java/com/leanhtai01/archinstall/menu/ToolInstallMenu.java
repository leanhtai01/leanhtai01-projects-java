package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.tool.Browser;
import com.leanhtai01.archinstall.osinstall.tool.CoreTool;
import com.leanhtai01.archinstall.osinstall.tool.DiskImageTool;
import com.leanhtai01.archinstall.osinstall.tool.Font;
import com.leanhtai01.archinstall.osinstall.tool.Multimedia;
import com.leanhtai01.archinstall.osinstall.tool.NetworkTool;
import com.leanhtai01.archinstall.osinstall.tool.Office;
import com.leanhtai01.archinstall.osinstall.tool.RemoteDesktop;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class ToolInstallMenu extends InstallMenu {
    public ToolInstallMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new InstallOption("Browser", new Browser(chrootDir, userAccount), false));
        addOption(new InstallOption("Core Tools", new CoreTool(chrootDir, userAccount), false));
        addOption(new InstallOption("Disk Image Tools", new DiskImageTool(chrootDir, userAccount), false));
        addOption(new InstallOption("Font", new Font(chrootDir), false));
        addOption(new InstallOption("Multimedia", new Multimedia(chrootDir), false));
        addOption(new InstallOption("Network Tool", new NetworkTool(chrootDir), false));
        addOption(new InstallOption("Office", new Office(chrootDir, userAccount), false));
        addOption(new InstallOption("Remote Desktop", new RemoteDesktop(chrootDir), false));
    }
}
