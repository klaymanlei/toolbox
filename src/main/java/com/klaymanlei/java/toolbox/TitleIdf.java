package com.klaymanlei.java.toolbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleIdf {
    public void readData(String date, String path, Map<String, Word> dataMap) throws IOException {
        List<String> lines = FileOper.readData(path);
        parse(date, lines, dataMap);
    }

    public Map<String, Word> cal(Map<String, Word> dataMap, int day) throws IOException {
        Map<String, Word> rsMap = new HashMap<String, Word>();
        for (String title : dataMap.keySet()) {
            Word word = dataMap.get(title);
            int ocDay = word.dateMap.size(); // 短语出现过几天
            Word idf = new Word();
            idf.word = word.word;
            rsMap.put(title, idf);
            for (String date : word.dateMap.keySet()) {
                Double[] vv = word.dateMap.get(date);
                idf.dateMap.put(date, new Double[]{vv[0], vv[1] / ((double)ocDay / day)});
            }
        }
        return rsMap;
    }

    public static void parse(String date, List<String> lines, Map<String, Word> dataMap) {
//        dataMap.clear();
        for (String line : lines) {
            String[] strs = line.split("\t");
            if (strs.length < 3)
                continue;
            Double[] vv = new Double[2];
            try{
                vv[0] = Double.parseDouble(strs[1]);
                vv[1] = Double.parseDouble(strs[2]);
            }catch (NumberFormatException e) {
                continue;
            }
            String title = strs[0];
            Word word = dataMap.get(title);
            if (word == null) {
                word = new Word();
                word.word = title;
                dataMap.put(title, word);
            }
            word.dateMap.put(date, vv);
        }
    }

    public static void main(String[] args) throws IOException {
        String[] strs = {"07-28", "07-29", "07-30", "07-31", "08-01", "08-02", "08-03", "08-04", "08-05", "08-06", "08-07", "08-08", "08-09", "08-10"};
        Map<String, Word> dataMap = new HashMap<String, Word>();
        TitleIdf idf = new TitleIdf();
        int day = 0;
        for (String s : strs) {
            day++;
            String path = "/home/leidayu/dev/sina/zmodem/videotitle.2017-" + s + ".tf";
            System.out.println(path);
            idf.readData("2017-" + s, path, dataMap);
        }
        Map<String, Word> idfMap = idf.cal(dataMap, day);
        output(idfMap);
    }

    public static void output(Map<String, Word> idfMap) throws IOException {
        // 输出
        Map<String, List<String>> outMap = new HashMap<String, List<String>>();
        for (String w : idfMap.keySet()) {
            for (String date : idfMap.get(w).dateMap.keySet()) {
                List<String> lines = outMap.get(date);
                if (lines == null) {
                    lines = new ArrayList<String>();
                    outMap.put(date, lines);
                }
                lines.add(w + "\t" + idfMap.get(w).dateMap.get(date)[0] + "\t" + idfMap.get(w).dateMap.get(date)[1]);
            }
        }
        for (String date : outMap.keySet()) {
            FileOper.writeData(outMap.get(date), "/home/leidayu/dev/sina/zmodem/videotitle." + date + ".tfidf");
        }
    }

    private static class Word {
        private String word;
        // Double[] = {vv, tf}
        private Map<String, Double[]> dateMap = new HashMap<String, Double[]>();
    }
}
