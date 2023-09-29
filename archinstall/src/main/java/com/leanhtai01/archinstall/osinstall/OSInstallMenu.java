package com.leanhtai01.archinstall.osinstall;

import static com.leanhtai01.lib.InputValidation.chooseIntegerOption;
import static com.leanhtai01.lib.InputValidation.displayMenu;
import static com.leanhtai01.lib.InputValidation.isAnswerYes;
import static com.leanhtai01.lib.InputValidation.isValidIntegerChoice;
import static com.leanhtai01.lib.InputValidation.markInstall;
import static com.leanhtai01.lib.InputValidation.unmarkInstall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.leanhtai01.archinstall.osinstall.desktopenvironment.DesktopEnvironmentInstallMenu;
import com.leanhtai01.archinstall.osinstall.driver.DriverInstallMenu;
import com.leanhtai01.archinstall.osinstall.programming.ProgrammingInstallMenu;
import com.leanhtai01.archinstall.osinstall.tool.ToolInstallMenu;
import com.leanhtai01.archinstall.osinstall.virtualmachine.VirtualMachineInstallMenu;
import com.leanhtai01.archinstall.systeminfo.SystemInfo;
import com.leanhtai01.archinstall.systeminfo.UserAccount;
import com.leanhtai01.lib.InputValidation;

public class OSInstallMenu extends InstallMenu {
    private List<InstallMenu> installMenus;

    public OSInstallMenu(String chrootDir, UserAccount userAccount) {
        super(chrootDir, userAccount);

        installMenus = new ArrayList<>();
        installMenus.add(new BaseSystemInstall(null, userAccount));
        installMenus.add(new DesktopEnvironmentInstallMenu(chrootDir, userAccount));
        installMenus.add(new DriverInstallMenu(chrootDir, userAccount));
        installMenus.add(new ProgrammingInstallMenu(chrootDir, userAccount));
        installMenus.add(new ToolInstallMenu(chrootDir, userAccount));
        installMenus.add(new VirtualMachineInstallMenu(chrootDir, userAccount));

        menu.add("Install Base System");
        menu.add("Install Desktop Environment");
        menu.add("Install Drivers");
        menu.add("Install Programming Environment");
        menu.add("Install Tools");
        menu.add("Install Virtual Machine Tools");
    }

    @Override
    public Set<Integer> selectOptions() {
        int choice = chooseIntegerOption(() -> displayMenu(menu, "? "),
                getMinChoice(), getMaxChoice(), getExitOption());
        while (choice != getExitOption()) {
            if (isValidIntegerChoice(choice, getMinChoice(), getMaxChoice())) {
                choices.add(choice);
                selectSubMenuOptions(choice);
            } else {
                System.console().printf("Invalid choice. Please try again!%n");
            }

            System.console().printf("%n");
            choice = chooseIntegerOption(() -> displayMenu(menu, "? "),
                    getMinChoice(), getMaxChoice(), getExitOption());
        }

        return choices;
    }

    private void selectSubMenuOptions(int choice) {
        var installMenu = installMenus.get(choice);
        installMenu.selectOptions();
        if (!installMenu.getChoices().isEmpty()) {
            markInstall(menu, choice, InputValidation.CHECK_MARK);
        } else {
            choices.remove(choice);
            unmarkInstall(menu, choice);
        }
    }

    @Override
    public void install() throws IOException, InterruptedException {
        System.console().printf("%s%n", getInstallSummary());
        for (InstallMenu installMenu : installMenus) {
            if (!(installMenu instanceof BaseSystemInstall)) {
                System.console().printf("%s%n", installMenu.getInstallSummary());
            }
        }

        System.console().printf(":: Proceed with installation? [Y/n] ");
        String answer = System.console().readLine();

        if (isAnswerYes(answer)) {
            if (choices.contains(0)) {
                SystemInfo systemInfo = new SystemInfo();
                systemInfo.getSystemInfo();

                if (installMenus.get(0) instanceof BaseSystemInstall baseSystemInstall) {
                    baseSystemInstall.setSystemInfo(systemInfo);
                    baseSystemInstall.install();
                }
            }

            for (InstallMenu installMenu : installMenus) {
                installMenu.install();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OSInstallMenu osInstallMenu = new OSInstallMenu(null, null);
        osInstallMenu.selectOptions();
        osInstallMenu.install();
    }
}
