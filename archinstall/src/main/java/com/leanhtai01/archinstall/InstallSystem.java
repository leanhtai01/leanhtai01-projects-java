package com.leanhtai01.archinstall;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.leanhtai01.archinstall.menu.DesktopEnvironmentMenu;
import com.leanhtai01.archinstall.menu.DriverMenu;
import com.leanhtai01.archinstall.menu.PartitionLayoutMenu;
import com.leanhtai01.archinstall.menu.ProgrammingMenu;
import com.leanhtai01.archinstall.menu.ToolMenu;
import com.leanhtai01.archinstall.menu.VirtualMachineMenu;
import com.leanhtai01.archinstall.osinstall.BaseSystem;
import com.leanhtai01.archinstall.partition.PartitionLayout;
import com.leanhtai01.archinstall.systeminfo.SystemInfo;
import com.leanhtai01.archinstall.systeminfo.UserAccount;
import com.leanhtai01.archinstall.util.IOUtil;
import com.leanhtai01.archinstall.util.NetworkUtil;

public class InstallSystem implements Runnable {
    private SystemInfo systemInfo;
    private UserAccount userAccount;

    private void getInfo() throws IOException, InterruptedException {
        String[] mirrorsArray = new String[3];
        Arrays.fill(mirrorsArray, "Server = https://mirror.xtom.com.hk/archlinux/$repo/os/$arch");
        List<String> mirrors = Arrays.asList(mirrorsArray);

        System.console().printf("Hostname: ");
        final String hostname = System.console().readLine();

        final String rootPassword = IOUtil.readPassword(
                "Root's password: ",
                "Re-enter root's password: ");

        System.console().printf("User's real name: ");
        final String realName = System.console().readLine();

        System.console().printf("Username: ");
        final String username = System.console().readLine();

        final String userPassword = IOUtil.readPassword(
                "User's password: ",
                "Re-enter User's password: ");

        final PartitionLayout partitionLayout = new PartitionLayoutMenu().selectPartitionLayout();

        systemInfo = new SystemInfo(hostname, rootPassword, mirrors, partitionLayout);
        userAccount = new UserAccount(realName, username, userPassword);
    }

    @Override
    public void run() {
        try {
            getInfo();

            final String chrootDir = "/mnt";
            BaseSystem baseSystem = new BaseSystem(systemInfo, userAccount);

            DesktopEnvironmentMenu desktopEnvironmentMenu = new DesktopEnvironmentMenu(chrootDir, userAccount);
            desktopEnvironmentMenu.setOptions(Set.of(0));

            DriverMenu driverMenu = new DriverMenu(chrootDir, userAccount);
            driverMenu.selectAll();

            ProgrammingMenu programmingMenu = new ProgrammingMenu(chrootDir, userAccount);
            programmingMenu.selectAll();

            ToolMenu toolMenu = new ToolMenu(chrootDir, userAccount);
            toolMenu.selectAll();

            VirtualMachineMenu virtualMachineMenu = new VirtualMachineMenu(chrootDir, userAccount);
            virtualMachineMenu.selectAll();

            System.console().printf("Install summary:%n");
            System.console().printf("%s%n", "[Base System]");
            System.console().printf("%s%n", desktopEnvironmentMenu.getActionSummary());
            System.console().printf("%s%n", driverMenu.getActionSummary());
            System.console().printf("%s%n", programmingMenu.getActionSummary());
            System.console().printf("%s%n", toolMenu.getActionSummary());
            System.console().printf("%s%n", virtualMachineMenu.getActionSummary());

            System.console().printf(":: Proceed with installation? [Y/n] ");
            String answer = System.console().readLine();
            if (IOUtil.isAnswerYes(answer)) {
                NetworkUtil.connectToWifi();
                baseSystem.install();
                desktopEnvironmentMenu.doAction();
                driverMenu.doAction();
                programmingMenu.doAction();
                toolMenu.doAction();
                virtualMachineMenu.doAction();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
