package il.ac.sce.ir.metric.core.score;

import il.ac.sce.ir.metric.core.config.Constants;

import java.io.Serializable;
import java.util.*;

public class Score implements Serializable, ReportedProperties {

    private final static double DEFAULT_ALPHA = 0.5D;

    private static final Set<String> REPORTED_PROPERTIES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    Constants.PRECISION,
                    Constants.RECALL,
                    Constants.ALPHA,
                    Constants.F1_MEASURE
            )));

    private final double precision;
    private final double recall;
    private final double alpha;
    private final Double f1;

    public Score(double alpha, double precision, double recall) {
        this.alpha = alpha;
        this.precision = precision;
        this.recall = recall;
        double factor = (1 - alpha) * precision + alpha * recall;
        if (factor > 0) {
            f1 = (precision * recall) / factor;
        } else {
            f1 = null;
        }
    }

    public Score(double precision, double recall) {
        this(DEFAULT_ALPHA, precision, recall);
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

    public Double getF1Measure() {
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

    public static class Builder {

        private Double precision;

        private Double recall;

        private Double alpha;

        public Builder precision(double precision) {
            this.precision = precision;
            return this;
        }

        public Builder recall(double recall) {
            this.recall = recall;
            return this;
        }

        public Builder alpha(double alpha) {
            this.alpha = alpha;
            return this;
        }

        public Score build() {
            if (precision == null || recall == null) {
                throw new IllegalArgumentException("Either precision or recall is not provided." +
                        " Both should be provided");
            }
            if (alpha == null) {
                return new Score(precision, recall);
            }
            return new Score(precision, recall, alpha);
        }
    }
}
