package com.klaymanlei.java.toolbox;

import java.io.IOException;
import java.util.*;

public class WordPositionAnalyzer {

    public static void toElements(List<String> wordList, Map<String, Double> resultMap) {
        if (wordList == null)
            return;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < wordList.size(); i++) {
            String firstWord = wordList.get(i);
            buf.append(firstWord);
            for (int j = 1; j < 8 && i + j < wordList.size(); j++) {
                String keyword = firstWord + wordList.get(i + j);
                Double num = resultMap.remove(keyword);
                if (num == null) {
                    num = (double)j;
                }else {
                    num = num > j ? j : num;
                }
                resultMap.put(keyword, num);
            }
        }
        resultMap.remove(buf.toString());
        resultMap.put(buf.toString(), -1d);
    }

    public static void main(String[] args) throws IOException {
        // 读取title的VV数据
        String path = "/home/leidayu/dev/sina/zmodem/videotitle_vv_top.out";
        Map<String, Long> data = FileOper.readData(path);

//        Map<String, Long> data = new HashMap<String, Long>();
//        data.put("C-Oli Chanel", 6509l);
//        data.put("C-Oli Raf Sim", 23l);
//        data.put("C-Oli 出品，天后席琳迪翁出镜时装短片Cline Dion Takes Paris in the Best Couture Looks of the Season，拍摄于秋冬巴黎高定周期间，身着最新>高级定制华服的Cline drama十足，大显喜", 1l);
//        data.put("MDZZ！当人类模仿起人工", 20l);
//        data.put("MDZZ！当人类模仿起人工智障", 20l);
//        data.put("NBA历史上每支球队的最佳", 20l);
//        data.put("NBA历史上的经典cros", 20l);
//        data.put("[爽爽侃车]奔驰G63 AMG", 20l);
//        data.put("[爽爽侃车]奔驰G63 AMG_新浪视频", 20l);
//        data.put("“林肯公园”主唱家中死亡", 20l);
//        data.put("“林肯公园”主唱自杀 年仅", 20l);
//        data.put("“纳沙”“海棠”双台风·中央气象台：“纳沙”持续减弱已停止编号_新浪视频", 20l);
//        data.put("“纳沙”携手“海棠”  双台风呼风唤雨·国家防总：启动防汛防台风Ⅲ级应急响应", 20l);
//        data.put("“海棠”今晨登陆福建合并“纳沙”北上：双台风合并将一路北上_新浪视频", 20l);
//        data.put("“路怒症”爆发！ 只因对方车技差", 20l);
//        data.put("“路怒症”爆发！奔驰司机故意多次撞宾利车 只因对方车技差", 20l);
//        data.put("“遍地开花”本周暑热将同袭南北方·宁夏：高温范围或突破历史极值", 20l);
//        data.put("“遍地开花”本周暑热将同袭南北方·山东淄博：连续3天发布高温橙色预警信号", 20l);
//        data.put("《七十二层奇楼》吴亦凡 清唱《你还要我怎样》+《小幸运》歌手凡Mr_凡先生", 20l);
//        data.put("《七十二层奇楼》吴亦凡、赵丽颖联手淘汰侯明昊", 20l);
//        data.put("《三生三世》完美大结局 赵又廷桃花林拥吻杨幂", 20l);
//        data.put("《三生三世》，浅浅和夜华那场经典的床戏，杨幂赵又廷原声曝光，剧本给大幂幂的台词是你是忍不住了吗？，配音版本却换了，导演，为什么要改？", 20l);
//        data.put("《中国新闻》 中印磋商寻求解决边境对峙事件", 20l);
//        data.put("《中国新闻》 中印陆军反恐联合训练将在印举行", 20l);
//        data.put("《中国新闻》 中国外交部：中方敦促巴以双方立即实现停火", 20l);
//        data.put("《中国新闻》 中国将实施税务黑名单制度", 20l);
//        data.put("《变形金刚5》中文预告", 20l);
//        data.put("《变形金刚5》官方预告_新浪视频", 20l);

        // 计算文字段的VV
        List<Sentence> titles = new ArrayList<Sentence>();
        for (String title : data.keySet()) {
            Sentence sentence = new Sentence();
            sentence.sentence = title;
            sentence.vv = data.get(title);
            titles.add(sentence);
            List<List<String>> chars = SentenceOper.pretreat(title);
            for (List<String> list : chars) {
                toElements(list, sentence.wordPos);
            }
        }
        List<List<Sentence>> evaluated = evaluate(titles);
        TreeMap<Long, List<List<Sentence>>> ordered = new TreeMap<Long, List<List<Sentence>>>();
        for (List<Sentence> slist : evaluated) {
            long vv = 0;
            for (Sentence s : slist) {
                vv += s.vv;
            }
            List<List<Sentence>> list = ordered.get(vv);
            if (list == null) {
                list = new ArrayList<List<Sentence>>();
                ordered.put(vv, list);
            }
            list.add(slist);
        }
        int n = 0;
        while (ordered.size() > 0) {
            n++;
            if (n > 200)
                break;
            long key = ordered.lastKey();
            System.out.println(ordered.remove(key));
        }
    }

    public static List<List<Sentence>> evaluate(List<Sentence> titles) {
        List<List<Sentence>> evaluatedList = new ArrayList<List<Sentence>>();
        int n = 0;
        for (Sentence title : titles) {
            n++;
            if (title.vv < 100)
                continue;
            if (n % 1000 == 0)
                System.out.println(n + " : " + evaluatedList.size());
            double minDistance = Double.MAX_VALUE;
            List<Sentence> matchedTitle = null;
            for (List<Sentence> evaluated : evaluatedList) {
                double min = Double.MAX_VALUE;
                double max = 0.25;
                for (Sentence e : evaluated) {
                    double dist = distance(e, title);
                    if (dist > 0.3) {
                        break;
                    }
                    if (dist < min) min = dist;
                    if (dist > max) max = dist;
                }
                if (max < 0.25 && min < minDistance && min < 0.22) {
                    minDistance = min;
                    matchedTitle = evaluated;
                }
            }
            if (matchedTitle == null) {
                matchedTitle = new ArrayList<Sentence>();
                matchedTitle.add(title);
                evaluatedList.add(matchedTitle);
            } else {
                matchedTitle.add(title);
            }
        }
        return evaluatedList;
    }

    public static void add(Sentence evaluated, Sentence title) {
        evaluated.count++;
        evaluated.sentence += "\n" + title.sentence;
        evaluated.vv += title.vv;
        for (String word : title.wordPos.keySet()) {
            double value = title.wordPos.get(word);
            Double evaluatedValue = evaluated.wordPos.remove(value);
            if (evaluatedValue == null) {
                evaluatedValue = 0d;
            }
            evaluatedValue += value;
            evaluated.wordPos.put(word, evaluatedValue);
        }
    }

    public static double distance(Sentence evaluated, Sentence title) {
        double result = 0;
        double adj = 0;
        for (String key : title.wordPos.keySet()) {
            double pos1 = title.wordPos.get(key) / title.count;
            Double pos2 = evaluated.wordPos.get(key);
            if (pos2 == null)
                pos2 = 0d;
            if (pos1 == -1) {
                if (pos2 == -1) {
                    // 两个标题中包含同样的短句时对最终结果进行-2%的调整
                    adj += 0.1;
                } else {
                    result += Math.pow(pos1, 2);
                }
            } else {
                pos2 /= evaluated.count;
                if (pos1 == 1 && pos2 == 1)
                    adj += 0.05;
                result += Math.pow(pos1 - pos2, 2);
            }
        }
        for (String key : evaluated.wordPos.keySet()) {
            if (title.wordPos.containsKey(key))
                continue;
            result += Math.pow(evaluated.wordPos.get(key) / evaluated.count, 2);
        }
        int len = (title.wordPos.size() > 100 ? 100 : title.wordPos.size())
                + (evaluated.wordPos.size() > 100 ? 100 : evaluated.wordPos.size());
        return Math.sqrt(result) / len * (1 - adj);
    }

    private static class Sentence {
        private int count = 1;
        private String sentence;
        private Map<String, Double> wordPos = new HashMap<String, Double>();
        private long vv;

        @Override
        public String toString() {
            return "Sentence{" +
                    "sentence='" + sentence + '\'' +
                    ", vv=" + vv +
                    '}';
        }
    }
}
