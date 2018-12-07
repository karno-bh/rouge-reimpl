package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

import il.ac.sce.ir.metric.core.config.Constants;

import java.util.HashMap;
import java.util.Map;

public class NGramTextConfig {

    private final int charMin;

    private final int charMax;

    private final int charDist;

    public NGramTextConfig(int charMin, int charMax, int charDist) {
        this.charMin = charMin;
        this.charMax = charMax;
        this.charDist = charDist;
    }

    public int getCharMin() {
        return charMin;
    }

    public int getCharMax() {
        return charMax;
    }

    public int getCharDist() {
        return charDist;
    }

    public static NGramTextConfig fromMap(Map<String, Object> map) {
        // int min, max, dist;
        int min = Integer.parseInt(map.get(Constants.MIN).toString());
        int max = Integer.parseInt(map.get(Constants.MAX).toString());
        int dist = Integer.parseInt(map.get(Constants.DIST).toString());
        return new NGramTextConfig(min, max, dist);
    }

    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put(Constants.MIN, getCharMin());
        map.put(Constants.MAX, getCharMax());
        map.put(Constants.DIST, getCharDist());
        return map;
    }

    public static class Builder {

        private int charMin;

        private int charMax;

        private int charDist;

        public Builder charMin(int charMin) {
            this.charMin = charMin;
            return this;
        }

        public Builder charMax(int charMax) {
            this.charMax = charMax;
            return this;
        }

        public Builder charDist(int charDist) {
            this.charDist = charDist;
            return this;
        }

        public NGramTextConfig build() {
            return new NGramTextConfig(charMin, charMax, charDist);
        }
    }
}
