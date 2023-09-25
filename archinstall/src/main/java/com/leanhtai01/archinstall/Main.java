package com.leanhtai01.archinstall;

import static com.leanhtai01.archinstall.util.PackageUtil.installFlatpakPackages;
import static com.leanhtai01.archinstall.util.PackageUtil.installPkgs;
import static com.leanhtai01.archinstall.util.ShellUtil.runSilent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.leanhtai01.archinstall.osinstall.BaseSystemInstall;
import com.leanhtai01.archinstall.osinstall.desktopenvironment.GNOMEInstall;
import com.leanhtai01.archinstall.osinstall.driver.IntelDriverInstall;
import com.leanhtai01.archinstall.osinstall.driver.PipeWireInstall;
import com.leanhtai01.archinstall.osinstall.driver.TLPInstall;
import com.leanhtai01.archinstall.osinstall.programming.CAndCPPInstall;
import com.leanhtai01.archinstall.osinstall.programming.CoreProgrammingInstall;
import com.leanhtai01.archinstall.osinstall.programming.DockerInstall;
import com.leanhtai01.archinstall.osinstall.programming.DotNETInstall;
import com.leanhtai01.archinstall.osinstall.programming.GTKProgrammingInstall;
import com.leanhtai01.archinstall.osinstall.programming.GoInstall;
import com.leanhtai01.archinstall.osinstall.programming.JavaInstall;
import com.leanhtai01.archinstall.osinstall.programming.JavaScriptInstall;
import com.leanhtai01.archinstall.osinstall.programming.Neo4jInstall;
import com.leanhtai01.archinstall.osinstall.programming.PythonInstall;
import com.leanhtai01.archinstall.osinstall.tool.BrowserInstall;
import com.leanhtai01.archinstall.osinstall.tool.CoreToolInstall;
import com.leanhtai01.archinstall.osinstall.tool.FontInstall;
import com.leanhtai01.archinstall.osinstall.tool.MultimediaInstall;
import com.leanhtai01.archinstall.osinstall.tool.OfficeInstall;
import com.leanhtai01.archinstall.osinstall.tool.RemoteDesktopInstall;
import com.leanhtai01.archinstall.osinstall.virtualmachine.KVMInstall;
import com.leanhtai01.archinstall.osinstall.virtualmachine.VirtualBoxInstall;
import com.leanhtai01.archinstall.partition.NormalPartitionLayout;
import com.leanhtai01.archinstall.partition.PartitionLayout;
import com.leanhtai01.archinstall.partition.PartitionLayoutMenu;
import com.leanhtai01.archinstall.systeminfo.StorageDeviceSize;
import com.leanhtai01.archinstall.systeminfo.UserAccount;
import com.leanhtai01.archinstall.systeminfo.WirelessNetwork;
import com.leanhtai01.lib.InputValidation;

public class Main {
    private static final String CHROOT_DIR = "/mnt";
    private static final String HOST_NAME;
    private static final String ROOT_PASSWORD;
    private static final UserAccount USER_ACCOUNT;
    private static final List<String> mirrors;
    private static final BaseSystemInstall baseSystemInstall;

    static {
        System.console().printf("Hostname: ");
        HOST_NAME = System.console().readLine();

        ROOT_PASSWORD = InputValidation.readPasswordFromConsole(
                "Root's password: ",
                "Re-enter root's password: ",
                "Two password isn't the same. Please try again!%n");

        System.console().printf("User's real name: ");
        final String realName = System.console().readLine();

        System.console().printf("Username: ");
        final String username = System.console().readLine();

        final String userPassword = InputValidation.readPasswordFromConsole(
                "User's password: ",
                "Re-enter User's password: ",
                "Two password isn't the same. Please try again!%n");

        USER_ACCOUNT = new UserAccount(realName, username, userPassword);

        PartitionLayout partitionLayout;
        try {
            partitionLayout = PartitionLayoutMenu.getPartitionLayout();
        } catch (IOException | InterruptedException e) {
            partitionLayout = new NormalPartitionLayout("sda",
                    new StorageDeviceSize(550L, "M"), new StorageDeviceSize(550L, "M"),
                    new StorageDeviceSize(1L, "G"));
            Thread.currentThread().interrupt();
        }

        String[] mirrorsArray = new String[3];
        Arrays.fill(mirrorsArray, "Server = https://mirror.xtom.com.hk/archlinux/$repo/os/$arch");
        mirrors = Arrays.asList(mirrorsArray);

        baseSystemInstall = new BaseSystemInstall(partitionLayout, mirrors, HOST_NAME,
                ROOT_PASSWORD, USER_ACCOUNT);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int choice = InputValidation.chooseIntegerOption(Main::displayMainMenu, 1, 4);

        switch (choice) {
            case 1 -> baseSystemInstall.install();
            case 2 -> installLightweightSystem();
            case 3 -> installFullSystem();
            case 4 -> {
                configureGNOME();
                installFlatpakPkgs();
            }
            default -> System.console().printf("Invalid choice!");
        }
    }

    private static void displayMainMenu() {
        System.console().printf("Menu option:%n");
        System.console().printf("1. Install base system%n");
        System.console().printf("2. Install lightweight system%n");
        System.console().printf("3. Install full system%n");
        System.console().printf("4. Install Flatpak packages, configure GNOME%n");
        System.console().printf("? ");
    }

    public static void installLightweightSystem() throws IOException, InterruptedException {
        connectToWifi();

        baseSystemInstall.install();

        GNOMEInstall gnomeInstall = new GNOMEInstall(CHROOT_DIR, USER_ACCOUNT);
        gnomeInstall.install();
        gnomeInstall.config();

        RemoteDesktopInstall remoteDesktopInstall = new RemoteDesktopInstall(CHROOT_DIR, USER_ACCOUNT);
        remoteDesktopInstall.install();
        remoteDesktopInstall.config();

        PipeWireInstall pipeWireInstall = new PipeWireInstall(CHROOT_DIR, USER_ACCOUNT);
        pipeWireInstall.install();
        pipeWireInstall.config();

        installPkgs(List.of("firefox"), USER_ACCOUNT, CHROOT_DIR);
    }

    private static void installFullSystem() throws InterruptedException, IOException {
        connectToWifi();

        baseSystemInstall.install();

        GNOMEInstall gnomeInstall = new GNOMEInstall(CHROOT_DIR, USER_ACCOUNT);
        gnomeInstall.install();
        gnomeInstall.config();

        RemoteDesktopInstall remoteDesktopInstall = new RemoteDesktopInstall(CHROOT_DIR, USER_ACCOUNT);
        remoteDesktopInstall.install();
        remoteDesktopInstall.config();

        IntelDriverInstall intelDriverInstall = new IntelDriverInstall(CHROOT_DIR);
        intelDriverInstall.install();
        intelDriverInstall.config();

        PipeWireInstall pipeWireInstall = new PipeWireInstall(CHROOT_DIR, USER_ACCOUNT);
        pipeWireInstall.install();
        pipeWireInstall.config();

        TLPInstall tlpInstall = new TLPInstall(CHROOT_DIR, USER_ACCOUNT);
        tlpInstall.install();
        tlpInstall.config();

        CAndCPPInstall cAndCPPInstall = new CAndCPPInstall(CHROOT_DIR, USER_ACCOUNT);
        cAndCPPInstall.install();
        cAndCPPInstall.config();

        CoreProgrammingInstall coreProgrammingInstall = new CoreProgrammingInstall(CHROOT_DIR, USER_ACCOUNT);
        coreProgrammingInstall.install();
        coreProgrammingInstall.config();

        DockerInstall dockerInstall = new DockerInstall(CHROOT_DIR, USER_ACCOUNT);
        dockerInstall.install();
        dockerInstall.config();

        DotNETInstall dotNETInstall = new DotNETInstall(CHROOT_DIR, USER_ACCOUNT);
        dotNETInstall.install();
        dotNETInstall.config();

        GoInstall goInstall = new GoInstall(CHROOT_DIR, USER_ACCOUNT);
        goInstall.install();
        goInstall.config();

        GTKProgrammingInstall gtkProgrammingInstall = new GTKProgrammingInstall(CHROOT_DIR, USER_ACCOUNT);
        gtkProgrammingInstall.install();
        gtkProgrammingInstall.config();

        JavaInstall javaInstall = new JavaInstall(CHROOT_DIR, USER_ACCOUNT);
        javaInstall.install();
        javaInstall.config();

        JavaScriptInstall javaScriptInstall = new JavaScriptInstall(CHROOT_DIR, USER_ACCOUNT);
        javaScriptInstall.install();
        javaScriptInstall.config();

        Neo4jInstall neo4jInstall = new Neo4jInstall(CHROOT_DIR, USER_ACCOUNT);
        neo4jInstall.install();
        neo4jInstall.config();

        PythonInstall pythonInstall = new PythonInstall(CHROOT_DIR, USER_ACCOUNT);
        pythonInstall.install();
        pythonInstall.config();

        BrowserInstall browserInstall = new BrowserInstall(CHROOT_DIR, USER_ACCOUNT);
        browserInstall.install();
        browserInstall.config();

        CoreToolInstall coreToolInstall = new CoreToolInstall(CHROOT_DIR, USER_ACCOUNT);
        coreToolInstall.install();
        coreToolInstall.config();

        FontInstall fontInstall = new FontInstall(CHROOT_DIR);
        fontInstall.install();
        fontInstall.config();

        MultimediaInstall multimediaInstall = new MultimediaInstall(CHROOT_DIR, USER_ACCOUNT);
        multimediaInstall.install();
        multimediaInstall.config();

        OfficeInstall officeInstall = new OfficeInstall(CHROOT_DIR, USER_ACCOUNT);
        officeInstall.install();
        officeInstall.config();

        KVMInstall kvmInstall = new KVMInstall(CHROOT_DIR, USER_ACCOUNT);
        kvmInstall.install();
        kvmInstall.config();

        VirtualBoxInstall virtualBoxInstall = new VirtualBoxInstall(CHROOT_DIR, USER_ACCOUNT);
        virtualBoxInstall.install();
        virtualBoxInstall.config();
    }

    public static void connectToWifi() throws InterruptedException, IOException {
        boolean isConnected = isConnectedToInternet();

        if (!isConnected) {
            System.console().printf("Connecting to Wifi...%n");

            do {
                System.console().printf("SSID: ");
                String ssid = System.console().readLine();

                System.console().printf("Password: ");
                String password = String.valueOf(System.console().readPassword());

                WirelessNetwork network = new WirelessNetwork(ssid, password, "wlan0", true);
                network.connect();
                TimeUnit.SECONDS.sleep(5);

                isConnected = isConnectedToInternet();

                if (!isConnected) {
                    System.console().printf("Invalid SSID or password. Please try again!%n");
                }

            } while (!isConnected);
        }
    }

    public static void configureGNOME() throws InterruptedException, IOException {
        GNOMEInstall gnomeInstall = new GNOMEInstall(null, USER_ACCOUNT);
        gnomeInstall.configureDesktopInterface();
        gnomeInstall.createCustomShortcut(gnomeInstall.readShortcutsFromFile("gnome-shortcuts.txt"));
        gnomeInstall.configureIbusBamboo();
        gnomeInstall.enableExtension("appindicatorsupport@rgcjonas.gmail.com");
    }

    public static void installFlatpakPkgs() throws InterruptedException, IOException {
        installFlatpakPackages(List.of("org.goldendict.GoldenDict", "com.belmoussaoui.Authenticator"));
    }

    private static boolean isConnectedToInternet() throws IOException, InterruptedException {
        return runSilent(List.of("ping", "-c", "3", "www.google.com")) == 0;
    }
}
