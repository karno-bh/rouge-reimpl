package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import java.util.Objects;

public class ProcessedChunk<T> {

    private final ProcessedCategory processedCategory;

    private final ProcessedSystem processedSystem;

    private final String metric;

    private final String peerFileName;

    private final String model;

    private final String topic;

    private final ProcessedChunkType chunkType;

    private final T chunkData;

    public ProcessedChunk(ProcessedCategory processedCategory,
                          ProcessedSystem processedSystem,
                          String metric,
                          String peerFileName,
                          String model,
                          String topic,
                          ProcessedChunkType chunkType,
                          T chunkData) {
        this.processedCategory = processedCategory;
        this.processedSystem = processedSystem;
        this.metric = metric;
        this.peerFileName = peerFileName;
        this.model = model;
        this.topic = topic;
        this.chunkType = chunkType;
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

    public String getModel() {
        return model;
    }

    public String getTopic() {
        return topic;
    }

    public ProcessedChunkType getChunkType() {
        return chunkType;
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

        private String model;

        private String topic;

        private ProcessedChunkType chunkType = ProcessedChunkType.PEER_MULTI_MODEL;

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

        public Builder<U> model(String model) {
            this.model = model;
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

        public Builder<U> chunkType(ProcessedChunkType chunkType) {
            this.chunkType = chunkType;
            switch (chunkType) {
                case PEER_MULTI_MODEL:
                    this.checkTopicData = false;
                    this.checkPeerFileName = true;
                    break;
                case TOPIC:
                    this.checkTopicData = true;
                    this.checkPeerFileName = false;
                    break;
                default:
                    throw new IllegalStateException("Unknown chunk type");
            }
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
            this.model = processedChunk.getModel();
            this.metric = processedChunk.getMetric();
            this.peerFileName = processedChunk.getPeerFileName();
            this.topic = processedChunk.getTopic();
            this.chunkType = processedChunk.getChunkType();
            return this;
        }

        public ProcessedChunk<U> build() {
            Objects.requireNonNull(chunkType, "Chunk type should be not null");
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
                    metric, peerFileName, model, topic, chunkType,chunkData);
        }
    }
}
