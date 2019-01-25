package il.ac.sce.ir.metric.core.statistics;

import il.ac.sce.ir.metric.core.utils.math.DecimalRangeAnalyzer;

import java.math.BigDecimal;
import java.util.List;

public class NotchedBoxData {

    private final double[] data;
    private final Quantile quantile;

    private int lowIndex;
    private int upIndex;
    private double upNotch;
    private double lowNotch;

    /**
     * @param data - Data MUST be sorted!
     */
    public NotchedBoxData(double data[]) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be empty");
        }
        this.data = data;
        this.quantile = new Quantile(data);
        this.quantile.quartiles();
        this.quantile.calcIQR();
    }

    private int findIndex(double value, int direction) {
        int l = 0, r = data.length - 1, diff;

        while ((diff = r - l) > 1) {
            int middleIndex = diff / 2 + l;
            double middle = data[middleIndex];
            if (middle == value) {
                return middleIndex;
            } else if (value > middle) {
                l = middleIndex;
            } else {
                r = middleIndex;
            }
        }
        if (direction > 0) {
            return r;
        }
        return l;
    }

    public void adjucentValues() {

        double  iqrVal = quantile.getIqr(),
                q25 = quantile.getFirstQuartile(),
                q75 = quantile.getThirdQuartile();

        double spreadIqr = 1.5d * iqrVal;
        double upBound = q75 + spreadIqr;
        int lastIndex = data.length - 1;
        if (upBound >= data[lastIndex]) {
            this.upIndex = lastIndex;
        } else {
            this.upIndex = findIndex(upBound, -1);
        }

        double lowBound = q25 - spreadIqr;
        if (lowBound <= data[0]) {
            this.lowIndex = 0;
        } else {
            this.lowIndex = findIndex(lowBound, 1);
        }
    }

    public void notches() {
        double  median = quantile.getMedian(),
                iqrVal = quantile.getIqr();

        double factor = 1.57d * iqrVal / Math.sqrt(data.length);
        this.lowNotch = median - factor;
        this.upNotch = median + factor;
    }

    public Quantile getQuantile() {
        return quantile;
    }

    public int getLowIndex() {
        return lowIndex;
    }

    public int getUpIndex() {
        return upIndex;
    }

    public double getUpNotch() {
        return upNotch;
    }

    public double getLowNotch() {
        return lowNotch;
    }

    public double[] getData() {
        return data;
    }

}
