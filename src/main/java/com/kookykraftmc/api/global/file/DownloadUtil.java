package com.kookykraftmc.api.global.file;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DownloadUtil {

    public static void download(File to, String address, CopyOption option) throws Exception {
        Files.copy(download(address), to.toPath(), option);
    }

    public static void download(File to, String address) throws Exception {
        download(to, address, StandardCopyOption.REPLACE_EXISTING);
    }

    public static InputStream download(String address) throws Exception {
        return new URL(address).openStream();
    }

}
