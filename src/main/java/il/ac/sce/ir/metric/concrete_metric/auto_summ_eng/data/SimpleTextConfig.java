package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

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
