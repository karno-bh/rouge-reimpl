package il.ac.sce.ir.metric.concrete_metric.elena.reporter;

import il.ac.sce.ir.metric.core.async_action.AsyncPeerAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;

import java.util.*;
import java.util.concurrent.Future;

public class ElenaReadabilityPeersReporter extends AbstractPeerReporter<Future<ProcessedPeer<ReadabilityMetricScore>>> {

    private ElenaReadabilityMetricScoreCalculator scoreCalculator;

    public ElenaReadabilityMetricScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(ElenaReadabilityMetricScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    protected void processConcretePeer(ProcessedPeer<Void> processedPeer, List<Future<ProcessedPeer<ReadabilityMetricScore>>> scoresCollector) {
        Text<String> peerText = Text.asFileLocation(getFileSystemPath().combinePath(processedPeer.getProcessedSystem().getDirLocation(),
                processedPeer.getPeerFileName()));

        ProcessedPeer<Text<String>> peerToProcess = new ProcessedPeer.Builder<Text<String>>()
                .processedCategory(processedPeer.getProcessedCategory())
                .processedSystem(processedPeer.getProcessedSystem())
                .metric(processedPeer.getMetric())
                .peerFileName(processedPeer.getPeerFileName())
                .peerData(peerText)
                .build();

        AsyncScoreCalculator<Text<String>, ReadabilityMetricScore> task = new AsyncScoreCalculator<>(peerToProcess, getScoreCalculator());
        Future<ProcessedPeer<ReadabilityMetricScore>> taskFuture = getExecutorService().submit(task);
        scoresCollector.add(taskFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedPeer<ReadabilityMetricScore>>> scoresCollector) {
        AsyncPeerAllResultsProcessor<ReadabilityMetricScore> asyncPeerAllResultsProcessor = new AsyncPeerAllResultsProcessor<>(scoresCollector,
                getConfiguration(), getArbiter(), getMetricName());
        getExecutorService().submit(asyncPeerAllResultsProcessor);
        /*try {
            asyncPeerAllResultsProcessor.call();
        } catch (Exception e) {
            getLogger().error("Error while dumping results onto the disk");
        }*/
    }

}
