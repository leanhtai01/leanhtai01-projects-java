package com.leanhtai01.archinstall.util;

import java.io.IOException;

public final class ShellUtil {
    private ShellUtil() {
    }

    public static void run(String... command) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.inheritIO();
        builder.command(command);
        builder.start().waitFor();
    }
}
