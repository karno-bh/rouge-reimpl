package il.ac.sce.ir.metric.concrete_metric.elena.reporter;

import il.ac.sce.ir.metric.core.async_action.AsyncPeerAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;

import java.util.*;
import java.util.concurrent.Future;

public class ElenaReadabilityPeersReporter extends AbstractPeerReporter<Future<ProcessedChunk<ReadabilityMetricScore>>> {

    private ElenaReadabilityMetricScoreCalculator scoreCalculator;

    public ElenaReadabilityMetricScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(ElenaReadabilityMetricScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    protected void processConcretePeer(ProcessedChunk<Void> processedChunk, List<Future<ProcessedChunk<ReadabilityMetricScore>>> scoresCollector) {
        Text<String> peerText = Text.asFileLocation(getFileSystemPath().combinePath(processedChunk.getProcessedSystem().getDirLocation(),
                processedChunk.getPeerFileName()));

        ProcessedChunk<Text<String>> peerToProcess = new ProcessedChunk.Builder<Text<String>>()
                .processedCategory(processedChunk.getProcessedCategory())
                .processedSystem(processedChunk.getProcessedSystem())
                .metric(processedChunk.getMetric())
                .peerFileName(processedChunk.getPeerFileName())
                .chunkData(peerText)
                .build();

        AsyncScoreCalculator<Text<String>, ReadabilityMetricScore> task = new AsyncScoreCalculator<>(peerToProcess, getScoreCalculator());
        Future<ProcessedChunk<ReadabilityMetricScore>> taskFuture = getExecutorService().submit(task);
        scoresCollector.add(taskFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedChunk<ReadabilityMetricScore>>> scoresCollector) {
        AsyncPeerAllResultsProcessor<ReadabilityMetricScore> asyncPeerAllResultsProcessor = new AsyncPeerAllResultsProcessor<>(scoresCollector,
                getConfiguration(), getArbiter(), getMetricName());
        getExecutorService().submit(asyncPeerAllResultsProcessor);
    }

}
