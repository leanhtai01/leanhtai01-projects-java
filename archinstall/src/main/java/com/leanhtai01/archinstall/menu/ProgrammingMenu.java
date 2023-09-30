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

public class ProgrammingMenu extends MultiChoiceMenu {
    public ProgrammingMenu(String chrootDir, UserAccount userAccount) {
        super();
        addOption(new Option("C And C++", new CAndCPP(chrootDir), false));
        addOption(new Option("Core Programming Tools", new CoreProgrammingTool(chrootDir, userAccount), false));
        addOption(new Option("Docker", new Docker(chrootDir, userAccount), false));
        addOption(new Option("DotNET", new DotNET(chrootDir), false));
        addOption(new Option("Go", new Go(chrootDir), false));
        addOption(new Option("GTK Programming Tools", new GTKProgrammingTool(chrootDir), false));
        addOption(new Option("Java", new Java(chrootDir, userAccount), false));
        addOption(new Option("JavaScript", new JavaScript(chrootDir, userAccount), false));
        addOption(new Option("Neo4j", new Neo4j(chrootDir, userAccount), false));
        addOption(new Option("Python", new Python(chrootDir), false));
    }
}
