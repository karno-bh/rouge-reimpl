package il.ac.sce.ir.metric.core.async_action;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.score.ReportedProperties;

import java.text.MessageFormat;
import java.util.function.Function;

public class ChunkTypeFunctionsFactory {

    public <T extends ReportedProperties> Function<ProcessedChunk<T>, String> getFileNameCombiner(ProcessedChunk<T> processedChunk) {
        switch (processedChunk.getChunkType()) {
            case PEER_MULTI_MODEL:
                return chunk -> {
                    MessageFormat keyFormat = new MessageFormat("{0}_{1}_{2}");
                    return keyFormat.format(new Object[]{
                            chunk.getProcessedCategory().getDirLocation(),
                            chunk.getProcessedSystem().getDirLocation(),
                            chunk.getMetric()
                    });
                };
            case TOPIC:
                return chunk -> {
                    MessageFormat keyFormat = new MessageFormat("{0}_{1}_{2}");
                    String realTopic = chunk.getTopic().substring(0, 4);
                    return keyFormat.format(new Object[]{
                            chunk.getProcessedCategory().getDirLocation(),
                            realTopic,
                            chunk.getMetric()
                    });
                };
            default:
                throw new IllegalStateException("Unknown chunk type");
        }
    }

}
