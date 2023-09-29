package com.leanhtai01.archinstall.menu;

import com.leanhtai01.archinstall.osinstall.programming.CAndCPP;
import com.leanhtai01.archinstall.osinstall.programming.CoreProgrammingTool;
import com.leanhtai01.archinstall.osinstall.programming.Docker;
import com.leanhtai01.archinstall.osinstall.programming.DotNET;
import com.leanhtai01.archinstall.osinstall.programming.GTKProgrammingTool;
import com.leanhtai01.archinstall.osinstall.programming.Go;
import com.leanhtai01.archinstall.osinstall.programming.Java;
import com.leanhtai01.archinstall.osinstall.programming.JavaScript;
import com.leanhtai01.archinstall.osinstall.programming.Neo4j;
import com.leanhtai01.archinstall.osinstall.programming.Python;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class ProgrammingInstallMenu extends InstallMenu {
    public ProgrammingInstallMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new InstallOption("C And C++", new CAndCPP(chrootDir), false));
        addOption(new InstallOption("Core Programming Tools", new CoreProgrammingTool(chrootDir, userAccount), false));
        addOption(new InstallOption("Docker", new Docker(chrootDir, userAccount), false));
        addOption(new InstallOption("DotNET", new DotNET(chrootDir), false));
        addOption(new InstallOption("Go", new Go(chrootDir), false));
        addOption(new InstallOption("GTK Programming Tools", new GTKProgrammingTool(chrootDir), false));
        addOption(new InstallOption("Java", new Java(chrootDir, userAccount), false));
        addOption(new InstallOption("JavaScript", new JavaScript(chrootDir, userAccount), false));
        addOption(new InstallOption("Neo4j", new Neo4j(chrootDir, userAccount), false));
        addOption(new InstallOption("Python", new Python(chrootDir), false));
    }
}
