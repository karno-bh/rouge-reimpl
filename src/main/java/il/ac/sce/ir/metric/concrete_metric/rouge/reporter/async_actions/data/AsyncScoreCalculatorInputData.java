package il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions.data;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;

import java.util.Objects;

public class AsyncScoreCalculatorInputData {

    private final ProcessedCategory processedCategory;

    private final ProcessedSystem processedSystem;

    private final String metric;

    private final String peerFileName;

    private final MultiModelPair multiModelPair;

    public AsyncScoreCalculatorInputData(ProcessedCategory processedCategory,
                                         ProcessedSystem processedSystem,
                                         String metric,
                                         String peerFileName,
                                         MultiModelPair multiModelPair) {
        this.processedCategory = processedCategory;
        this.processedSystem = processedSystem;
        this.metric = metric;
        this.peerFileName = peerFileName;
        this.multiModelPair = multiModelPair;
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

    public MultiModelPair getMultiModelPair() {
        return multiModelPair;
    }

    public static class Builder {

        private ProcessedCategory processedCategory;

        private ProcessedSystem processedSystem;

        private String metric;

        private String peerFileName;

        private MultiModelPair multiModelPair;

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

        public Builder multiModelPair(MultiModelPair multiModelPair) {
            this.multiModelPair = multiModelPair;
            return this;
        }

        public AsyncScoreCalculatorInputData build() {
            Objects.requireNonNull(processedCategory, "Processed Category should not be null");
            Objects.requireNonNull(processedSystem, "Processed System should not be null");
            Objects.requireNonNull(metric, "Metric should not be null");
            Objects.requireNonNull(peerFileName, "Peer File Name should not be null");
            Objects.requireNonNull(multiModelPair, "Multi Model Pair should not be null");

            return new AsyncScoreCalculatorInputData(
                    processedCategory,
                    processedSystem,
                    metric,
                    peerFileName,
                    multiModelPair
            );
        }
    }
}
