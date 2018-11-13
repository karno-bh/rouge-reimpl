package il.ac.sce.ir.metric.concrete_metric.rouge.reporter.data;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.Score;

import java.util.Objects;

public class ReportedBundle {

    private final ProcessedCategory processedCategory;

    private final ProcessedSystem processedSystem;

    private final String metric;

    private final String peerFileName;

    private final Score score;

    public ReportedBundle(ProcessedCategory processedCategory,
                          ProcessedSystem processedSystem,
                          String metric,
                          String peerFileName,
                          Score score) {
        this.processedCategory = processedCategory;
        this.processedSystem = processedSystem;
        this.metric = metric;
        this.peerFileName = peerFileName;
        this.score = score;
    }

    public ProcessedCategory getProcessedCategory() {
        return processedCategory;
    }

    public ProcessedSystem getProcessedSystem() {
        return processedSystem;
    }

    public String getMetric() {
        return metric;
    }

    public String getPeerFileName() {
        return peerFileName;
    }

    public Score getScore() {
        return score;
    }

    public static class Builder {

        private ProcessedCategory processedCategory;

        private ProcessedSystem processedSystem;

        private String metric;

        private String peerFileName;

        private Score score;


        public Builder processedCategory(ProcessedCategory processedCategory) {
            this.processedCategory = processedCategory;
            return this;
        }

        public Builder processedSystem(ProcessedSystem processedSystem) {
            this.processedSystem = processedSystem;
            return this;
        }

        public Builder metric(String metric) {
            this.metric = metric;
            return this;
        }

        public Builder peerFileName(String peerFileName) {
            this.peerFileName = peerFileName;
            return this;
        }

        public Builder score(Score score) {
            this.score = score;
            return this;
        }

        public ReportedBundle build() {
            Objects.requireNonNull(processedCategory, "Processed Category should not be null");
            Objects.requireNonNull(processedSystem, "Processed System should not be null");
            Objects.requireNonNull(metric, "Metric should not be null");
            Objects.requireNonNull(peerFileName, "Peer File Name should not be null");
            Objects.requireNonNull(score, "Score should not be null");

            return new ReportedBundle(processedCategory,
                    processedSystem,
                    metric,
                    peerFileName,
                    score);
        }
    }

}
