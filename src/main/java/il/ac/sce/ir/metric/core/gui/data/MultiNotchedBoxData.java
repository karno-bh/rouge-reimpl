package il.ac.sce.ir.metric.core.gui.data;

import il.ac.sce.ir.metric.core.statistics.NotchedBoxData;
import il.ac.sce.ir.metric.core.utils.StringUtils;
import il.ac.sce.ir.metric.core.utils.math.DecimalRangeAnalyzer;

import java.math.BigDecimal;
import java.util.*;

public class MultiNotchedBoxData {

    private final boolean sortWhenAdded;

    private double totalMax = Double.NEGATIVE_INFINITY;

    private double totalMin = Double.POSITIVE_INFINITY;

    private List<BigDecimal> scale;

    // preserve the order of adding values
    private final Map<String, NotchedBoxData> boxesByLabel = new LinkedHashMap<>();

    public MultiNotchedBoxData(boolean sortWhenAdded) {
        this.sortWhenAdded = sortWhenAdded;
    }

    public void add(String label, double[] data) {
        if (new StringUtils().isEmpty(label)) {
            throw new IllegalArgumentException("Box Label should not be empty");
        }

        if (sortWhenAdded) {
            double[] copied = new double[data.length];
            System.arraycopy(data, 0, copied, 0, data.length);
            data = copied;
            Arrays.sort(data);
        }

        // update min
        if (data[0] < totalMin) {
            totalMin = data[0];
        }

        // update max
        int lastElIdx = data.length - 1;
        if (data[lastElIdx] > totalMax) {
            totalMax = data[lastElIdx];
        }

        NotchedBoxData notchedBoxData = new NotchedBoxData(data);
        notchedBoxData.adjucentValues();
        notchedBoxData.notches();

        boxesByLabel.put(label, notchedBoxData);
    }

    public Map<String, NotchedBoxData> getBoxesByLabel() {
        return boxesByLabel;
    }

    public double[] getShrunkMinMax(double factor) {
        double diff = totalMax - totalMin;
        double shrunkDiff = diff * factor;
        double shrunkDelta = (shrunkDiff - diff) / 2d;
        return new double[] {
                totalMin - shrunkDelta,
                totalMax + shrunkDelta
        };
    }

    public List<BigDecimal> generateScale() {
        DecimalRangeAnalyzer decimalRangeAnalyzer = new DecimalRangeAnalyzer(totalMin, totalMax);
        return decimalRangeAnalyzer.decimalRange();
    }
}