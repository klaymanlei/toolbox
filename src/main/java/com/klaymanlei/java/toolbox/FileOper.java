package com.klaymanlei.java.toolbox;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileOper {

    public static void writeData(List<String> lines, String path) throws IOException {
        FileUtils.writeLines(new File(path), lines);
    }

    public static void appendData(List<String> lines, String path) throws IOException {
        FileUtils.writeLines(new File(path), lines, true);
    }

    public static List<String> readData(String path) throws IOException {
        List<String> lines = FileUtils.readLines(new File(path));
        return lines;
    }
}
