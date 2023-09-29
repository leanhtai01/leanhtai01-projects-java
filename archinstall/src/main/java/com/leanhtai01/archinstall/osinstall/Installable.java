package com.leanhtai01.archinstall.osinstall;

import java.io.IOException;

public interface Installable {
    int install() throws InterruptedException, IOException;

    default int config() throws IOException, InterruptedException {
        return 0;
    }
}
