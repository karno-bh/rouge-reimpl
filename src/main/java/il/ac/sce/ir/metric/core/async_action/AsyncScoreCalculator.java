package il.ac.sce.ir.metric.core.async_action;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.score.ReportedProperties;
import il.ac.sce.ir.metric.core.score_calculator.ScoreCalculator;

import java.util.concurrent.Callable;

public class AsyncScoreCalculator<X, Y extends ReportedProperties> implements Callable<ProcessedChunk<Y>> {

    private final ProcessedChunk<X> processedChunk;

    private final ScoreCalculator<X, Y> scoreCalculator;

    public AsyncScoreCalculator(ProcessedChunk<X> processedChunk, ScoreCalculator<X, Y> scoreCalculator) {
        this.processedChunk = processedChunk;
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public ProcessedChunk<Y> call() throws Exception {
        Y score = scoreCalculator.computeScore(processedChunk.getChunkData());
        return new ProcessedChunk.Builder<Y>()
                .cloneWithoutData(processedChunk)
                .chunkData(score)
                .build();
    }
}
