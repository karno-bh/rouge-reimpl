package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import java.util.Objects;

public class ProcessedChunk<T> {

    private final ProcessedCategory processedCategory;

    private final ProcessedSystem processedSystem;

    private final String metric;

    private final String peerFileName;

    private final String topic;

    private final T chunkData;

    public ProcessedChunk(ProcessedCategory processedCategory,
                          ProcessedSystem processedSystem,
                          String metric,
                          String peerFileName,
                          String topic,
                          T chunkData) {
        this.processedCategory = processedCategory;
        this.processedSystem = processedSystem;
        this.metric = metric;
        this.peerFileName = peerFileName;
        this.topic = topic;
        this.chunkData = chunkData;
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

    public String getTopic() {
        return topic;
    }

    public T getChunkData() {
        return chunkData;
    }

    public static <Delegate> Builder<Delegate> as() {
        return new Builder<>();
    }

    public static class Builder<U> {

        private ProcessedCategory processedCategory;

        private ProcessedSystem processedSystem;

        private String metric;

        private String peerFileName;

        private String topic;

        private U chunkData;

        private boolean checkChunkData = true;

        private boolean checkTopicData = false;

        private boolean checkPeerFileName = true;

        private boolean fromClone = false;

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

        public Builder<U> chunkData(U chunkData) {
            this.chunkData = chunkData;
            return this;
        }

        public Builder<U> topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder<U> checkChunkData(boolean checkChunkData) {
            this.checkChunkData = checkChunkData;
            return this;
        }

        public Builder<U> checkTopicData(boolean checkTopicData) {
            this.checkTopicData = checkTopicData;
            return this;
        }

        public Builder<U> checkPeerFileName(boolean checkPeerFileName) {
            this.checkPeerFileName = checkPeerFileName;
            return this;
        }

        public Builder<U> cloneWithoutData(ProcessedChunk<?> processedChunk) {
            this.fromClone = true;
            this.processedCategory = processedChunk.getProcessedCategory();
            this.processedSystem = processedChunk.getProcessedSystem();
            this.metric = processedChunk.getMetric();
            this.peerFileName = processedChunk.getPeerFileName();
            this.topic = processedChunk.getTopic();
            return this;
        }

        public ProcessedChunk<U> build() {
            Objects.requireNonNull(processedCategory, "Processed Category should not be null");
            if (!fromClone && checkPeerFileName) {
                Objects.requireNonNull(processedSystem, "Processed System should not be null");
            }
            Objects.requireNonNull(metric, "Metric should not be null");
            if (!fromClone && checkPeerFileName) {
                Objects.requireNonNull(peerFileName, "Peer File Name should not be null");
            }
            if (!fromClone && checkTopicData) {
                Objects.requireNonNull(topic, "Topic should not be null");
            }
            if (!fromClone && checkChunkData) {
                Objects.requireNonNull(chunkData, "Chunk Data should not be null");
            }

            return new ProcessedChunk<U>(processedCategory, processedSystem,
                    metric, peerFileName, topic, chunkData);
        }
    }
}
