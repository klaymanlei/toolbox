package com.klaymanlei.java.toolbox;

import java.io.IOException;
import java.util.*;

public class TitleTfIdf {
    public static final Double TF_IDF_THREHOLD = 0.03;

    public void readData(String date, String path, Map<String, Double[]> dataMap) throws IOException {
        List<String> lines = FileOper.readData(path);
        parse(date, lines, dataMap);
    }

    public static void parse(String date, List<String> lines, Map<String, Double[]> dataMap) {
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
            Double[] titleVV = dataMap.get(title);
            if (titleVV == null) {
                titleVV = vv;
                dataMap.put(title, titleVV);
            } else {
                titleVV[0] += vv[0];
                titleVV[1] = titleVV[1] > vv[1] ? titleVV[1] : vv[1];
            }
        }
    }

    public static TreeMap<Double, Set<String>> sort(Map<String, Double[]> dataMap) {
        TreeMap<Double, Set<String>> sorted = new TreeMap<Double, Set<String>>();
        for (String word : dataMap.keySet()) {
            Double[] vv = dataMap.get(word);
            if (vv[1] < TF_IDF_THREHOLD)
                continue;
            Set<String> set = sorted.get(vv[0]);
            if (set == null) {
                set = new HashSet<String>();
                sorted.put(vv[0], set);
            }
            set.add(word);
        }
        return sorted;
    }

    public static void main(String[] args) throws IOException {
        String[] strs = {"07-28", "07-29", "07-30", "07-31", "08-01", "08-02", "08-03"};
        //String[] strs = {"08-04", "08-05", "08-06", "08-07", "08-08", "08-09", "08-10"};
        Map<String, Double[]> dataMap = new HashMap<String, Double[]>();
        TitleTfIdf tfIdf = new TitleTfIdf();
        int day = 0;
        for (String s : strs) {
            day++;
            String path = "/home/leidayu/dev/sina/zmodem/videotitle.2017-" + s + ".tfidf";
            System.out.println(path);
            tfIdf.readData("2017-" + s, path, dataMap);
        }
        TreeMap<Double, Set<String>> sorted = sort(dataMap);
        Double key = Double.MAX_VALUE;
        while ((key = sorted.lowerKey(key)) != null) {
            for(String w : sorted.get(key)) {
                System.out.println(w + "\t" + key);
            }
        }
    }
}
