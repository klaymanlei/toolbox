package com.klaymanlei.java.toolbox;

import java.util.*;

public class SentenceAnalyzer {
    /*
    1、基本规格——针对汉字的一般集合（cp936，约等于GBK，共计20000多汉字）
    [^\dA-Za-z\u3007\u4E00-\u9FCB\uE815-\uE864]
    2、扩充规格——针对多一些的汉字（支持CJK ExtA，共计接近30000汉字）
    [^\dA-Za-z\u3007\u3400-\u4DB5\u4E00-\u9FCB\uE815-\uE864]
    3、豪华规格——针对更多的汉字（支持CJK ExtB、C、……，共计75000多汉字）
    (?![\dA-Za-z\u3007\u3400-\u4DB5\u4E00-\u9FCB\uE815-\uE864]|[\uD840-\uD87F][\uDC00-\uDFFF])
     */
    public static final String WORD_REG = "[^\\dA-Za-z\\u3007\\u4E00-\\u9FCB\\uE815-\\uE864]+";

    /**
     * 把一句话中非数字字母和汉字的符号替换成空格，之后按空格分成几个短剧，返回每个断句中的文字列表。
     * 注意：连续的英文和数字算作同一个字
     */
    public static List<List<String>> pretreat(String sentence) {
        String cleaned = cleaning(sentence); // 把非数字字母和汉字的符号替换成空格
        String[] strs = cleaned.trim().split(" ");
        List<List<String>> resultSet = new ArrayList<List<String>>();
        for (String str : strs) {
            resultSet.add(split(str));
        }
        return resultSet;
    }

    public static String cleaning(String str) {
        return str.replaceAll(WORD_REG, " ");
    }

    public static List<String> split(String str) {
        if (str.length() == 0)
            return null;
        List<String> list = new ArrayList<String>();
        int lastWordType = 1; // 上一个字符的类型，0：数字或字母；1：其他
        StringBuffer buf = new StringBuffer();
        for (Character c : str.toCharArray()) {
            if (Character.isDigit(c) || Character.isUpperCase(c) || Character.isLowerCase(c)) {
                // 连续的字母和数字算作一个单字
                lastWordType = 0;
                buf.append(c);
            }
            else {
                // 每个汉字作为一个单字
                if (lastWordType == 0) list.add(buf.toString().toLowerCase());
                buf.setLength(0);
                list.add(String.valueOf(c));
                lastWordType = 1;
            }
        }
        if (buf.length() > 0) {
            list.add(buf.toString().toLowerCase());
            buf.setLength(0);
        }
        return list;
    }

    // 把一句话按照每2个字、3个字、4个字、5个字、6个字拆成小文字段，并返回所有出现过的文字段
    public static Set<String> toElements(List<String> wordList) {
        Set<String> resultSet = new HashSet<String>();
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

    public static void main(String[] args) {
        List<List<String>> chars = pretreat("《中国新闻》2013年世界游泳锦标赛 孙杨卫冕800米自由泳金牌");
        Set<String> resultSet = new HashSet<String>();
        for (List<String> list : chars) {
            //System.out.println(list);
            Set<String> words = toElements(list);
            resultSet.addAll(words);
        }
        System.out.println(resultSet.size());
        for (String w: resultSet) {
            System.out.println(w);
        }
    }
}
