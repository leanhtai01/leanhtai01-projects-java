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
        addOption(new MultiChoiceOption("C And C++", new CAndCPP(chrootDir), false));
        addOption(new MultiChoiceOption("Core Programming Tools", new CoreProgrammingTool(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("Docker", new Docker(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("DotNET", new DotNET(chrootDir), false));
        addOption(new MultiChoiceOption("Go", new Go(chrootDir), false));
        addOption(new MultiChoiceOption("GTK Programming Tools", new GTKProgrammingTool(chrootDir), false));
        addOption(new MultiChoiceOption("Java", new Java(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("JavaScript", new JavaScript(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("Neo4j", new Neo4j(chrootDir, userAccount), false));
        addOption(new MultiChoiceOption("Python", new Python(chrootDir), false));
    }
}
