package com.klaymanlei.java.toolbox;

import java.io.IOException;
import java.util.*;

public class MainClass {

    public static final String PARENT_FOLDER = "/home/leidayu/dev/sina/zmodem/";

    public static void sentenceAnalyze(String[] strs) throws IOException {
        for (String s : strs) {
            String path = PARENT_FOLDER + "videotitle.2017-" + s + ".out";
            System.out.println(path);
            SentenceAnalyzer analyzer = new SentenceAnalyzer();
            TreeMap<Long, Set<String>> tmap = analyzer.analyze(path, 500);
            List<String> lines = new ArrayList<String>();
            for (Long vv : tmap.keySet()) {
                for (String word : tmap.get(vv)) {
                    lines.add(word + "\t" + vv);
                }
            }
            FileOper.writeData(lines, PARENT_FOLDER + "videotitle.2017-" + s + ".rs");
        }
    }

    public static void tf(String[] strs) throws IOException {
        for (String s : strs) {
            String path = PARENT_FOLDER + "videotitle.2017-" + s + ".rs";
            System.out.println(path);
            TitleTf tf = new TitleTf();
            Map<String, Double[]> tfMap = tf.cal(path);
            List<String> lines = new ArrayList<String>();
            for (String w : tfMap.keySet()) {
                //System.out.println(w + ": " + Arrays.toString(tfMap.get(w)));
                lines.add(w + "\t" + tfMap.get(w)[0] + "\t" + tfMap.get(w)[1]);
            }
            FileOper.writeData(lines, PARENT_FOLDER + "videotitle.2017-" + s + ".tf");
        }
    }

    public static void idf(String[] strs) throws IOException {
        Map<String, TitleIdf.Word> dataMap = new HashMap<String, TitleIdf.Word>();
        TitleIdf idf = new TitleIdf();
        int day = 0;
        for (String s : strs) {
            day++;
            String path = PARENT_FOLDER + "videotitle.2017-" + s + ".tf";
            System.out.println(path);
            idf.readData("2017-" + s, path, dataMap);
        }
        TitleIdf.output(idf.cal(dataMap, day));
    }

    public static void sort(String[] strs) throws IOException {
        Map<String, Double[]> dataMap = new HashMap<String, Double[]>();
        TitleTfIdf tfIdf = new TitleTfIdf();
        int day = 0;
        for (String s : strs) {
            day++;
            String path = PARENT_FOLDER + "videotitle.2017-" + s + ".tfidf";
            System.out.println(path);
            tfIdf.readData("2017-" + s, path, dataMap);
        }
        TreeMap<Double, Set<String>> sorted = TitleTfIdf.sort(dataMap);
        Double key = Double.MAX_VALUE;
        while ((key = sorted.lowerKey(key)) != null) {
            for(String w : sorted.get(key)) {
                System.out.println(w + "\t" + key);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //String[] allStrs = {"07-28", "07-29", "07-30", "07-31", "08-01", "08-02", "08-03", "08-04", "08-05", "08-06", "08-07", "08-08", "08-09", "08-10"};
        //String[] strs = {"07-28", "07-29", "07-30", "07-31", "08-01", "08-02", "08-03"};
        String[] strs = {"08-04", "08-05", "08-06", "08-07", "08-08", "08-09", "08-10"};
        sentenceAnalyze(strs);
        tf(strs);
        idf(strs);
        sort(strs);
    }
}
