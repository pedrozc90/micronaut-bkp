package com.pedrozc90.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {

    public static byte[] readAsBytes(final String filename) throws IOException {
        final File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("File not found: " + filename);
        }
        return Files.readAllBytes(file.toPath());
    }

    public static String readAsString(final String filename) throws IOException {
        byte[] encodedBytes = readAsBytes(filename);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

}
