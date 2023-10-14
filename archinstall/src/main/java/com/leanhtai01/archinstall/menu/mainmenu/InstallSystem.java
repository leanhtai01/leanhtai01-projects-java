package com.leanhtai01.archinstall.menu.mainmenu;

import static com.leanhtai01.archinstall.util.IOUtil.getConfirmation;
import static com.leanhtai01.archinstall.util.IOUtil.isAnswerYes;
import static com.leanhtai01.archinstall.util.IOUtil.readPassword;

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
import com.leanhtai01.archinstall.util.NetworkUtil;

public class InstallSystem implements Runnable {
    private SystemInfo systemInfo;
    private UserAccount userAccount;

    private BaseSystem baseSystem;

    protected DesktopEnvironmentMenu desktopEnvironmentMenu;
    protected DriverMenu driverMenu;
    protected ProgrammingMenu programmingMenu;
    protected ToolMenu toolMenu;
    protected VirtualMachineMenu virtualMachineMenu;

    @Override
    public void run() {
        try {
            getSystemInfo();
            getInstallInfo();
            getInstallSummary();
            confirmAndInstall();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void getSystemInfo() throws IOException, InterruptedException {
        String[] mirrorsArray = new String[3];
        Arrays.fill(mirrorsArray, "Server = https://mirror.xtom.com.hk/archlinux/$repo/os/$arch");
        List<String> mirrors = Arrays.asList(mirrorsArray);

        System.console().printf("Hostname: ");
        final String hostname = System.console().readLine();

        final String rootPassword = readPassword(
                "Root's password: ",
                "Re-enter root's password: ");

        System.console().printf("User's real name: ");
        final String realName = System.console().readLine();

        System.console().printf("Username: ");
        final String username = System.console().readLine();

        final String userPassword = readPassword(
                "User's password: ",
                "Re-enter User's password: ");

        final PartitionLayout partitionLayout = new PartitionLayoutMenu().selectPartitionLayout();

        systemInfo = new SystemInfo(hostname, rootPassword, mirrors, partitionLayout);
        userAccount = new UserAccount(realName, username, userPassword);
    }

    private void getInstallInfo() {
        final String chrootDir = "/mnt";

        baseSystem = new BaseSystem(systemInfo, userAccount);
        desktopEnvironmentMenu = new DesktopEnvironmentMenu(chrootDir, userAccount);
        driverMenu = new DriverMenu(chrootDir, userAccount);
        programmingMenu = new ProgrammingMenu(chrootDir, userAccount);
        toolMenu = new ToolMenu(chrootDir, userAccount);
        virtualMachineMenu = new VirtualMachineMenu(chrootDir, userAccount);

        selectInstallSoftwares();
    }

    protected void selectInstallSoftwares() {
        desktopEnvironmentMenu.setOptions(Set.of(0));
        driverMenu.selectAll();
        programmingMenu.selectAll();
        toolMenu.selectAll();
        virtualMachineMenu.selectAll();
    }

    private void getInstallSummary() {
        System.console().printf("Install summary:%n");
        System.console().printf("%s%n", "[Base System]");
        System.console().printf("%s%n", desktopEnvironmentMenu.getActionSummary());
        System.console().printf("%s%n", driverMenu.getActionSummary());
        System.console().printf("%s%n", programmingMenu.getActionSummary());
        System.console().printf("%s%n", toolMenu.getActionSummary());
        System.console().printf("%s%n", virtualMachineMenu.getActionSummary());
    }

    private void confirmAndInstall() throws InterruptedException, IOException {
        if (isAnswerYes(getConfirmation(":: Proceed with installation? [Y/n] "))) {
            NetworkUtil.connectToWifi();
            baseSystem.install();
            desktopEnvironmentMenu.doAction();
            driverMenu.doAction();
            programmingMenu.doAction();
            toolMenu.doAction();
            virtualMachineMenu.doAction();
        }
    }
}
