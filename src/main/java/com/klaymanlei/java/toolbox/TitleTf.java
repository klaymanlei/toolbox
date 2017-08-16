package com.klaymanlei.java.toolbox;

import java.io.IOException;
import java.util.*;

public class TitleTf {

    public Map<String, Double[]> cal(String path) throws IOException {
        List<String> lines = FileOper.readData(path);
        Map<String, Long> dataMap = new HashMap<String, Long>();
        long total = parse(lines, dataMap);
        Map<String, Double[]> tfMap = cal(dataMap, total);
        return tfMap;
    }

    public Map<String, Double[]> cal(Map<String, Long> dataMap, long total) throws IOException {
        Map<String, Double[]> rsMap = new HashMap<String, Double[]>();
        for (String word : dataMap.keySet()) {
            rsMap.put(word, new Double[]{(double) dataMap.get(word), (double) dataMap.get(word) / total});
        }
        return rsMap;
    }

    public static long parse(List<String> lines, Map<String, Long> dataMap) {
        long total = 0l;
        dataMap.clear();
        for (String line : lines) {
            int index = line.indexOf("\t");
            Long vv;
            try{
                vv = Long.parseLong(line.substring(index + 1));
            }catch (NumberFormatException e) {
                continue;
            }
            total += vv;
            String title = line.substring(0,index);
            Long value = dataMap.remove(title);
            if (value == null)
                value = 0l;
            dataMap.put(title, value + vv);
        }
        return total;
    }

    public static void main(String[] args) throws IOException {
        String[] strs = {"07-28", "07-29", "07-30", "07-31", "08-01", "08-02", "08-03", "08-04", "08-05", "08-06", "08-07", "08-08", "08-09", "08-10"};
        for (String s : strs) {
            String path = "/home/leidayu/dev/sina/zmodem/videotitle.2017-" + s + ".rs";
            System.out.println(path);
            TitleTf tf = new TitleTf();
            Map<String, Double[]> tfMap = tf.cal(path);
            List<String> lines = new ArrayList<String>();
            for (String w : tfMap.keySet()) {
                //System.out.println(w + ": " + Arrays.toString(tfMap.get(w)));
                lines.add(w + "\t" + tfMap.get(w)[0] + "\t" + tfMap.get(w)[1]);
            }
            FileOper.writeData(lines, "/home/leidayu/dev/sina/zmodem/videotitle.2017-" + s + ".tf");
        }
    }
}
