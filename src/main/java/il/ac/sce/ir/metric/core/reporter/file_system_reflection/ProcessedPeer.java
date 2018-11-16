package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import java.util.Objects;

public class ProcessedPeer<T> {

    private final ProcessedCategory processedCategory;

    private final ProcessedSystem processedSystem;

    private final String metric;

    private final String peerFileName;

    private final T peerData;

    public ProcessedPeer(ProcessedCategory processedCategory, ProcessedSystem processedSystem, String metric, String peerFileName,
                         T peerData) {
        this.processedCategory = processedCategory;
        this.processedSystem = processedSystem;
        this.metric = metric;
        this.peerFileName = peerFileName;
        this.peerData = peerData;
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

    public T getPeerData() {
        return peerData;
    }

    public static <Delegate> Builder<Delegate> as() {
        return new Builder<>();
    }

    public static class Builder<U> {

        private ProcessedCategory processedCategory;

        private ProcessedSystem processedSystem;

        private String metric;

        private String peerFileName;

        private boolean checkPeerData = true;

        private U peerData;

        public Builder<U> processedCategory(ProcessedCategory processedCategory) {
            this.processedCategory = processedCategory;
            return this;
        }

        public Builder<U> processedSystem(ProcessedSystem processedSystem) {
            this.processedSystem = processedSystem;
            return this;
        }

        public Builder<U> metric(String metric) {
            this.metric = metric;
            return this;
        }

        public Builder<U> peerFileName(String peerFileName) {
            this.peerFileName = peerFileName;
            return this;
        }

        public Builder<U> checkPeerData(boolean checkPeerData) {
            this.checkPeerData = checkPeerData;
            return this;
        }

        public Builder<U> peerData(U peerData) {
            this.peerData = peerData;
            return this;
        }

        public ProcessedPeer<U> build() {
            Objects.requireNonNull(processedCategory, "Processed Category should not be null");
            Objects.requireNonNull(processedSystem, "Processed System should not be null");
            Objects.requireNonNull(metric, "Metric should not be null");
            Objects.requireNonNull(peerFileName, "Peer File Name should not be null");
            if (checkPeerData) {
                Objects.requireNonNull(peerData, "Peer Data should not be null");
            }

            return new ProcessedPeer<U>(processedCategory, processedSystem,
                    metric, peerFileName, peerData);
        }
    }
}
