package il.ac.sce.ir.metric.core.statistics;

public class Quantile {

    private final double[] data;

    private double firstQuartile;

    private double secondQuartile;

    private double thirdQuartile;

    private double iqr;

    public Quantile(double data[]) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data should not be empty");
        }
        this.data = data;
    }

    public double quantile(double p) {
        final int n = data.length;
        final double[] d = data;

        IndexFraction
                l = pAtI(1, n),
                r = pAtI(n, n);
        if (p <= l.p) {
            return d[0];
        } else if (p >= r.p) {
            return d[n - 1];
        }

        int diff;
        while ((diff = r.index - l.index) > 1) {
            final int middleIndex = diff / 2 + l.index;
            final IndexFraction middle = pAtI(middleIndex, n);
            if (middle.p == p) {
                return d[middle.index - 1];
            } else if (p > middle.p) {
                l = middle;
            } else {
                r = middle;
            }
        }
        double f = (p - l.p) / (r.p - l.p);
        return (1 - f) * d[r.index - 1] + f * d[l.index - 1];
    }

    public void quartiles() {
        this.firstQuartile = quantile(0.25d);
        this.secondQuartile = quantile(0.5d);
        this.thirdQuartile = quantile(0.75d);
    }

    public double calcIQR() {
        iqr = thirdQuartile - firstQuartile;
        return iqr;
    }

    public double getFirstQuartile() {
        return firstQuartile;
    }

    public double getSecondQuartile() {
        return secondQuartile;
    }

    public double getThirdQuartile() {
        return thirdQuartile;
    }

    public double getIqr() {
        return iqr;
    }

    public double getMedian() {
        return secondQuartile;
    }

    private IndexFraction pAtI(int i, int n) {
        IndexFraction indexFraction = new IndexFraction();
        indexFraction.index = i;
        indexFraction.p = ((double) i - 0.5d) / (double) n;
        return indexFraction;
    }

    private static class IndexFraction {
        int index;
        double p;
    }


}
