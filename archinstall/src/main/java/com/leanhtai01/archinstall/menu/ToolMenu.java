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

public class ToolMenu extends MultiChoiceMenu {
    public ToolMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new Option("Browser", new Browser(chrootDir, userAccount), false));
        addOption(new Option("Core Tools", new CoreTool(chrootDir, userAccount), false));
        addOption(new Option("Disk Image Tools", new DiskImageTool(chrootDir, userAccount), false));
        addOption(new Option("Font", new Font(chrootDir), false));
        addOption(new Option("Multimedia", new Multimedia(chrootDir), false));
        addOption(new Option("Network Tool", new NetworkTool(chrootDir), false));
        addOption(new Option("Office", new Office(chrootDir, userAccount), false));
        addOption(new Option("Remote Desktop", new RemoteDesktop(chrootDir), false));
    }

    public static void main(String[] args) {
        ToolMenu toolMenu = new ToolMenu(null, null);
        toolMenu.selectOption();
        System.console().printf("%s%n", toolMenu.getActionSummary());
    }
}
