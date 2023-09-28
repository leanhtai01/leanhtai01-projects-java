package com.leanhtai01.archinstall.osinstall.programming;

import java.io.IOException;

import com.leanhtai01.archinstall.osinstall.InstallMenu;
import com.leanhtai01.archinstall.systeminfo.UserAccount;

public class ProgrammingInstallMenu extends InstallMenu {
    public ProgrammingInstallMenu(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);
        menu.add("Install Core tools");
        menu.add("Install C/C++");
        menu.add("Install GTK programming");
        menu.add("Install Go");
        menu.add("Install Java");
        menu.add("Install DotNET");
        menu.add("Install Python");
        menu.add("Install JavaScript");
        menu.add("Install Docker");
        menu.add("Install Neo4j");
    }

    @Override
    public void install() throws InterruptedException, IOException {
        for (Integer choice : choices) {
            switch (choice) {
                case 1 -> {
                    CoreProgrammingInstall coreProgrammingInstall = new CoreProgrammingInstall(chrootDir, userAccount);
                    coreProgrammingInstall.install();
                    coreProgrammingInstall.config();
                }
                case 2 -> {
                    CAndCPPInstall cAndCPPInstall = new CAndCPPInstall(chrootDir, userAccount);
                    cAndCPPInstall.install();
                    cAndCPPInstall.config();
                }
                case 3 -> {
                    GTKProgrammingInstall gtkProgrammingInstall = new GTKProgrammingInstall(chrootDir, userAccount);
                    gtkProgrammingInstall.install();
                    gtkProgrammingInstall.config();
                }
                case 4 -> {
                    GoInstall goInstall = new GoInstall(chrootDir, userAccount);
                    goInstall.install();
                    goInstall.config();
                }
                case 5 -> {
                    JavaInstall javaInstall = new JavaInstall(chrootDir, userAccount);
                    javaInstall.install();
                    javaInstall.config();
                }
                case 6 -> {
                    DotNETInstall dotNETInstall = new DotNETInstall(chrootDir, userAccount);
                    dotNETInstall.install();
                    dotNETInstall.config();
                }
                case 7 -> {
                    PythonInstall pythonInstall = new PythonInstall(chrootDir, userAccount);
                    pythonInstall.install();
                    pythonInstall.config();
                }
                case 8 -> {
                    JavaScriptInstall javaScriptInstall = new JavaScriptInstall(chrootDir, userAccount);
                    javaScriptInstall.install();
                    javaScriptInstall.config();
                }
                case 9 -> {
                    DockerInstall dockerInstall = new DockerInstall(chrootDir, userAccount);
                    dockerInstall.install();
                    dockerInstall.config();
                }
                case 10 -> {
                    Neo4jInstall neo4jInstall = new Neo4jInstall(chrootDir, userAccount);
                    neo4jInstall.install();
                    neo4jInstall.config();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid choice");
                }
            }
        }
    }
}
