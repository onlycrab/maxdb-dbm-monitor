package com.github.onlycrab.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * File reader for tests.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class TestFileReader {
    /**
     * Read file from {@code java.io.InputStream}.
     *
     * @param is InputStream for reading
     * @return list of lines
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readFile(InputStream is) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while ((line = reader.readLine()) != null) {
            lines.add(line.trim());
        }
        reader.close();
        return lines;
    }
}
