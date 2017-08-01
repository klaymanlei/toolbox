package com.klaymanlei.java.toolbox;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileOper {

    public static Map<String, Long> readData(String path) throws IOException {
        List<String> lines = FileUtils.readLines(new File(path));
        Map<String, Long> resultMap = new HashMap<String, Long>();
        for (String line : lines) {
            int index = line.indexOf("\t");
            Long vv;
            try{
                vv = Long.parseLong(line.substring(0,index));
            }catch (NumberFormatException e) {
                continue;
            }
            String title = line.substring(index + 1);
            Long value = resultMap.remove(title);
            if (value == null)
                value = 0l;
            resultMap.put(title, value + vv);
        }
        return resultMap;
    }

}
