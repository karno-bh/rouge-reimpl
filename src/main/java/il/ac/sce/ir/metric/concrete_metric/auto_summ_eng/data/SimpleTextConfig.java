package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

import il.ac.sce.ir.metric.core.config.Constants;

import java.util.HashMap;
import java.util.Map;

public class SimpleTextConfig {

    private final int wordMin;

    private final int wordMax;

    private final int wordDist;

    public SimpleTextConfig(int wordMin, int wordMax, int wordDist) {
        this.wordMin = wordMin;
        this.wordMax = wordMax;
        this.wordDist = wordDist;
    }

    public int getWordMin() {
        return wordMin;
    }

    public int getWordMax() {
        return wordMax;
    }

    public int getWordDist() {
        return wordDist;
    }

    public static SimpleTextConfig fromMap(Map<String, Object> map) {
        // int min, max, dist;
        int min = Integer.parseInt(map.get(Constants.MIN).toString());
        int max = Integer.parseInt(map.get(Constants.MAX).toString());
        int dist = Integer.parseInt(map.get(Constants.DIST).toString());
        return new SimpleTextConfig(min, max, dist);
    }

    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put(Constants.MIN, getWordMin());
        map.put(Constants.MAX, getWordMax());
        map.put(Constants.DIST, getWordDist());
        return map;
    }

    public static class Builder {

        private int wordMin;

        private int wordMax;

        private int wordDist;

        public Builder wordMin(int wordMin) {
            this.wordMin = wordMin;
            return this;
        }

        public Builder wordMax(int wordMax) {
            this.wordMax = wordMax;
            return this;
        }

        public Builder wordDist(int wordDist) {
            this.wordDist = wordDist;
            return this;
        }

        public SimpleTextConfig build() {
            return new SimpleTextConfig(wordMin, wordMax, wordDist);
        }

    }
}
