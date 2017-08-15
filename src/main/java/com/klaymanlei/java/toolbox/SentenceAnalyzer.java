package com.klaymanlei.java.toolbox;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.*;

public class SentenceAnalyzer {

    public static final int MAX_WORD_LEN = 5;

    // 把一句话按照每2个字、3个字、4个字、5个字、6个字拆成小文字段，并返回所有出现过的文字段
    public Set<String> toElements(List<String> wordList) {
        Set<String> resultSet = new HashSet<String>();
        if (wordList == null)
            return resultSet;
        StringBuffer buf = new StringBuffer();
        for (int i = 2; i < MAX_WORD_LEN + 1; i++) {
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
    public void resultCleaning(Map<String, Long> resultMap) {
        Set<String> delWord = new HashSet<String>();
        for (String word : resultMap.keySet()) {
            for (String otherWord : resultMap.keySet()) {
                if (otherWord.contains(word) && !otherWord.equals(word)) {
                    long wordVV = resultMap.get(word);
                    long otherVV = resultMap.get(otherWord);
                    if (((double)otherVV / wordVV) > (90d / 100))
                        delWord.add(word);
                }
            }
        }
        for (String word : delWord) {
            resultMap.remove(word);
        }
    }

    public TreeMap<Long, Set<String>> analyze(String path, int n) throws IOException {
        TreeMap<Long, Set<String>> tmap = analyze(path);
        merge(tmap);
        TreeMap<Long, Set<String>> interRs = splitWord(tmap);
        Map<String, Long> rs = new HashMap<String, Long>();
        while (interRs.size() > 0) {
            Long vv = interRs.lastKey();
            Set<String> words = interRs.remove(vv);
            for (String w : words) {
                Long vvRs = rs.remove(w);
                if (vvRs == null)
                    vvRs = 0l;
                vvRs += vv;
                rs.put(w, vvRs);
            }
        }
        return top(rs, n);
    }

    public TreeMap<Long, Set<String>> analyze(String path) throws IOException {
        // 读取title的VV数据
        Map<String, Long> data = FileOper.readData(path);
        Map<String, Long> resultMap = analyze(data);
        TreeMap<Long, Set<String>> tmap = top(resultMap, 5000);
        resultMap = distinct(tmap);
        tmap = top(resultMap, 5000);
        return tmap;
    }

    public Map<String, Long> analyze(Map<String, Long> data) {
        // 计算文字段的VV
        Map<String, Long> resultMap = new HashMap<String, Long>();
        for (String title : data.keySet()) {
            long vv = data.get(title);
            List<List<String>> chars = SentenceOper.pretreat(title);
            Set<String> resultSet = new HashSet<String>();
            // title拆成单词
            for (List<String> list : chars) {
                Set<String> words = toElements(list);
                resultSet.addAll(words);
            }
            // 按单词统计VV
            for (String word : resultSet) {
                Long value = resultMap.remove(word);
                if (value == null)
                    value = 0l;
                resultMap.put(word, value + vv);
            }
        }
        return resultMap;
    }

    public TreeMap<Long, Set<String>> top(Map<String, Long> resultMap, int n) {
        // 结果按VV排序，取前1000名
        TreeMap<Long, Set<String>> tmap = new TreeMap<Long, Set<String>>();
        for (String word : resultMap.keySet()) {
            Long vv = resultMap.get(word);
            Set<String> set = tmap.get(vv);
            if (set == null) {
                set = new HashSet<String>();
                tmap.put(vv, set);
                if (tmap.size() > n)
                    tmap.remove(tmap.firstKey());
            }
            set.add(word);
        }
        return tmap;
    }

    public Map<String, Long> distinct(TreeMap<Long, Set<String>> tmap) {
        // 清理包含在其他文字段中的段
        Map<String, Long> resultMap = new HashMap<String, Long>();
        for (Long vv : tmap.keySet()) {
            for (String word : tmap.get(vv)) {
                resultMap.put(word, vv);
            }
        }
        resultCleaning(resultMap);
        return resultMap;
    }

    // 尝试合并VV相同的多个文字段，如果一个文字段去掉第一个字和另一个文字段去掉最后一个字之后完全相同，则合并两个文字段
    public void merge(TreeMap<Long, Set<String>> wordMap) {
        for (Long key : wordMap.keySet()) {
            List<String> words = new ArrayList<String>();
            Set<String> set = wordMap.get(key);
            words.addAll(set);
            if (words.size() == 1)
                continue;
            merge(words);
            set.clear();
            set.addAll(words);
//            System.out.println(words);
//            System.out.println(wordMap.get(key));
        }
    }

    // 检查集合中的文字段之间是否可以合并
    public void merge(List<String> list) {
        int cantMergeCnt = 0;
        while (list.size() > 1 && list.size() > cantMergeCnt) {
            String word = list.remove(0);
            String mergedWord = merge(list, word);
            list.add(mergedWord);
            cantMergeCnt += 1;
        }
    }

    // 检查keyword是否和集合中的文字段可以合并
    public String merge(List<String> words, String keyword) {
        String mergedWord = keyword;
        for (int i = 0; i < words.size();) {
            if (mergedWord.equals(words.get(i))) {
                words.remove(i);
                continue;
            }
            String merged = merge(mergedWord, words.get(i));
            if (mergedWord.equals(merged)) {
                i++;
            }else {
                // 两个文字段合并成一个新的文字段后，重新与集合中所有文字段进行对比
                mergedWord = merged;
                words.remove(i);
                i = 0;
            }
        }
        return mergedWord;
    }

    // 检查两个文字段，如果只有第一个字或最后一个字不同，则合并两段文字
    public String merge(String merged, String word) {
        String longer = merged.length() > word.length() ? merged : word;
        String shorter = merged.length() > word.length() ? word : merged;
        if (longer.substring(1).contains(shorter.substring(0, shorter.length() - 1))) {
            return longer + shorter.charAt(shorter.length() - 1);
        }
        if (longer.substring(0, longer.length() - 1).contains(shorter.substring(1))) {
            return shorter.charAt(0) + longer;
        }
        return merged;
    }

    // 用VV较高的文字段对VV较低的文字段进行重新分词
    public TreeMap<Long, Set<String>> splitWord(TreeMap<Long, Set<String>> tmap) {
        TreeMap<Long, Set<String>> resultMap = new TreeMap<Long, Set<String>>();
        Node node = new Node();
        while (tmap.size() > 0) {
            Long vv = tmap.lastKey();
            Set<String> words = tmap.remove(vv);
            Set<String> splited = new HashSet<String>();
            for (String word : words) {
                Set<String> set = null;
                if (word.length() > MAX_WORD_LEN) {
                    set = splitWord(node, word);
                    splited.addAll(set);
                    if (set.size() != 1 || !set.contains(word)) {
                        continue;
                    }
                } else {
                    set = new HashSet<String>();
                    set.add(word);
                    splited.add(word);
                }
                node.add(set);
            }
            if (splited.size() > 0)
                resultMap.put(vv, splited);
        }
        return resultMap;
    }

    // 用保存的词库对word进行分词
    public Set<String> splitWord(Node node, String word) {
        Set<String> set = new HashSet<String>();
        int len = word.length();
        for (int i = len - 2; i >= 0; i--) {
            String sub = word.substring(i);
            int j = i;
            Node pointer = node;
            for (; j < word.length(); j++) {
                if (pointer.map.containsKey(String.valueOf(word.charAt(j)))) {
                    pointer = pointer.map.get(String.valueOf(word.charAt(j)));
                } else {
                    break;
                }
            }
            if (!pointer.map.containsKey("")) {
                j = i;
            }
            if (j > i) {
                if (j < word.length() - 1)
                    set.add(word.substring(j));
                word = word.substring(0, i);
            }
        }
        if(word.length() > 1)
            set.add(word);
        return set;
    }

    public static void main(String[] args) throws IOException {
        String[] strs = {"07-28", "07-29", "07-30", "07-31", "08-01", "08-02", "08-03", "08-04", "08-05", "08-06", "08-07", "08-08", "08-09", "08-10"};
        for (String s : strs) {
            String path = "/home/leidayu/dev/sina/zmodem/videotitle.2017-" + s + ".out";
            System.out.println(path);
            SentenceAnalyzer analyzer = new SentenceAnalyzer();
            TreeMap<Long, Set<String>> tmap = analyzer.analyze(path, 500);
            List<String> lines = new ArrayList<String>();
            for (Long vv : tmap.keySet()) {
                for (String word : tmap.get(vv)) {
                    lines.add(word + "\t" + vv);
                }
            }
            FileOper.writeData(lines, "/home/leidayu/dev/sina/zmodem/videotitle.2017-" + s + ".rs");
        }
    }

    private static class Node {
        Map<String, Node> map = new HashMap<String, Node>();
        void add(Set<String> set) {
            for (String word : set) {
                Map<String, Node> pointer = map;
                List<String> list = SentenceOper.split(word);
                for (String c : list) {
                    Node node = pointer.get(c);
                    if (node == null) {
                        node = new Node();
                        pointer.put(c, node);
                    }
                    pointer = node.map;
                }
                pointer.put("", null);
            }
        }
    }
}
