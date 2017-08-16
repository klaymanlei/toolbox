package com.klaymanlei.java.toolbox;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileOper {

    public static void writeData(List<String> lines, String path) throws IOException {
        FileUtils.writeLines(new File(path), lines);
    }

    public static List<String> readData(String path) throws IOException {
        List<String> lines = FileUtils.readLines(new File(path));
        return lines;
    }
}
