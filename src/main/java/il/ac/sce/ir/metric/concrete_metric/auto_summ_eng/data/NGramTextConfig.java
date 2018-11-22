package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

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
