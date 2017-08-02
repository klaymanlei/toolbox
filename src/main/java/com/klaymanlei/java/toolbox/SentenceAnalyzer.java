package com.klaymanlei.java.toolbox;

import java.io.IOException;
import java.util.*;

public class SentenceAnalyzer {

    // 把一句话按照每2个字、3个字、4个字、5个字、6个字拆成小文字段，并返回所有出现过的文字段
    public static Set<String> toElements(List<String> wordList) {
        Set<String> resultSet = new HashSet<String>();
        if (wordList == null)
            return resultSet;
        StringBuffer buf = new StringBuffer();
        for (int i = 2; i < 8; i++) {
            for (int j = 0; j < wordList.size() + 1 - i; j++) {
                buf.setLength(0);
                for (int k = 0; k < i; k++) {
                    buf.append(wordList.get(j+k));
                }
                resultSet.add(buf.toString());
            }
        }
        return resultSet;
    }

    /**
     * 结果中有互相包含的文字段，且两个文字段的VV相差很小的时候，取文字最长的一段
     * @param resultMap
     */
    public static void resultCleaning(Map<String, Long> resultMap) {
        Set<String> delWord = new HashSet<String>();
        int no = 1;
        for (String word : resultMap.keySet()) {
            if (no % 100 == 0)
                System.out.println(no + "/" + resultMap.size());
            no++;
            for (String otherWord : resultMap.keySet()) {
                if (otherWord.contains(word) && !otherWord.equals(word)) {
                    long wordVV = resultMap.get(word);
                    long otherVV = resultMap.get(otherWord);
                    if (((double)otherVV / wordVV) > (95d / 100))
                        delWord.add(word);
                }
            }
        }
        System.out.println(delWord.size() + ": 删除文字段");
        for (String word : delWord) {
            resultMap.remove(word);
        }
    }

    public static void main(String[] args) throws IOException {
        // 读取title的VV数据
        String path = "/home/leidayu/dev/sina/zmodem/videotitleVV.out";
        Map<String, Long> data = FileOper.readData(path);

        // 计算文字段的VV
        Map<String, Long> resultMap = new HashMap<String, Long>();
        int no = 1;
        for (String title : data.keySet()) {
            if (no % 10000 == 0)
                System.out.println(no + "/" + data.size());
            no++;
            long vv = data.get(title);
            List<List<String>> chars = SentenceOper.pretreat(title);
            Set<String> resultSet = new HashSet<String>();
            for (List<String> list : chars) {
                //System.out.println(list);
                Set<String> words = toElements(list);
                resultSet.addAll(words);
            }
            for (String word : resultSet) {
                Long value = resultMap.remove(word);
                if (value == null)
                    value = 0l;
                resultMap.put(word, value + vv);
            }
        }

        // 结果按VV排序，取前1000名
        System.out.println(resultMap.size());
        TreeMap<Long, Set<String>> tmap = new TreeMap<Long, Set<String>>();
        for (String word : resultMap.keySet()) {
            Long vv = resultMap.get(word);
            Set<String> set = tmap.get(vv);
            if (set == null) {
                set = new HashSet<String>();
                tmap.put(vv, set);
                if (tmap.size() > 1000)
                    tmap.remove(tmap.firstKey());
            }
            set.add(word);
        }

        // 清理包含在其他文字段中的段
        resultMap.clear();
        for(Long vv : tmap.keySet()) {
            for(String word : tmap.get(vv)) {
                resultMap.put(word, vv);
            }
        }
        resultCleaning(resultMap);

        // 结果按VV排序，取前1000名
        tmap.clear();
        for (String word : resultMap.keySet()) {
            Long vv = resultMap.get(word);
            Set<String> set = tmap.get(vv);
            if (set == null) {
                set = new HashSet<String>();
                tmap.put(vv, set);
                if (tmap.size() > 1000)
                    tmap.remove(tmap.firstKey());
            }
            set.add(word);
        }

        // 打印
        while(tmap.size() > 0) {
            System.out.println(tmap.lastKey() + ": " + tmap.remove(tmap.lastKey()));
        }
    }
}
