package com.klaymanlei.java.toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<String> pretreat(String str) {
        return split(cleaning(str));

    }

    public static String cleaning(String str) {
        return str.replaceAll(WORD_REG, " ");
    }

    public static List<String> split(String str) {
        if (str.length() == 0)
            return null;
        List<String> list = new ArrayList<String>();
        if (str.contains(" ")) {
            // 字符串包含空格的时候
            String[] subStrs = str.split(" ");
            for (String subStr : subStrs) {
                if (subStr.length() > 0)
                    list.addAll(split(subStr));
            }
            return list;
        } else {
            int i = 0;
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
    }

    public static List<Word> cal(List<String> wordList) {
        for (1
                10.39.3.)
        return null;
    }

    public static void main(String[] args) {
        System.out.println(pretreat("《中国新闻》2013年世界游泳锦标赛 孙杨卫冕800米自由泳金牌Cut"));
    }

    private static class Word {
        final int position;
        final String word;
        Map<String, Integer> properties = new HashMap<String, Integer>();
        Word(String word, int pos) {
            this.word = word;
            this.position = pos;
        }
    }
}
