package il.ac.sce.ir.metric.core.score;

import il.ac.sce.ir.metric.core.config.Constants;

import java.io.Serializable;
import java.util.*;

public class Score implements Serializable, ReportedProperties {

    private static final Set<String> REPORTED_PROPERTIES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    Constants.PRECISION,
                    Constants.RECALL,
                    Constants.ALPHA,
                    Constants.F1_MEASURE
            )));

    private double precision;
    private double recall;
    private double alpha = 0.5;
    private Double f1;

    public void setAlpha(double alpha) {
        this.alpha = alpha;
        f1 = null;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
        f1 = null;
    }

    public void setRecall(double recall) {
        this.recall = recall;
        f1 = null;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1Measure() {
        if (f1 != null) {
            return f1;
        }
        double factor = (1 - alpha) * precision + alpha * recall;
        if (factor > 0) {
            f1 = (precision * recall) / factor;
        }
        return f1;
    }

    @Override
    public String toString() {
        return "precision: " + precision + ", recall: " + recall + ", f1: " + getF1Measure() + ", alpha: " + alpha;
        /*return MessageFormat.format("precision: {0}, recall: {1}, f1: {2}, alpha: {3}",
                precision, recall, getF1Measure(), alpha);*/
    }

    @Override
    public Set<String> resolveReportedProperties() {
        return REPORTED_PROPERTIES;
    }
}
