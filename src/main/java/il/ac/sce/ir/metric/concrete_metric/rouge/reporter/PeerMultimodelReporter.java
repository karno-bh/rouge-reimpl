package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions.AsyncAllResultsProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemCommons;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PeerMultimodelReporter extends AbstractPeerReporter<Future<ProcessedPeer<Score>>> {

    private PeerMultimodelScoreCalculator scoreCalculator;

    public PeerMultimodelScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(PeerMultimodelScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    protected void processConcretePeer(ProcessedPeer<Void> processedPeer, List<Future<ProcessedPeer<Score>>> scoresCollector) {

        List<Text<String>> modelsPerPeer = getFileSystemTopologyResolver().getModelTextsPerPeer(getModelsDirectory(), processedPeer.getPeerFileName());
        if (modelsPerPeer == null || modelsPerPeer.isEmpty()) {
            getLogger().warn("No models found for peer {} in category {}", processedPeer.getPeerFileName(), processedPeer.getProcessedCategory().getDescription());
            return;
        }

        Text<String> peerText = Text.asFileLocation(getFileSystemPath().combinePath(processedPeer.getProcessedSystem().getDirLocation(),
                processedPeer.getPeerFileName()));
        MultiModelPair multiModelPair = new MultiModelPair(peerText, modelsPerPeer);

        ProcessedPeer<MultiModelPair> peerToProcess = new ProcessedPeer.Builder<MultiModelPair>()
                .processedCategory(processedPeer.getProcessedCategory())
                .processedSystem(processedPeer.getProcessedSystem())
                .metric(processedPeer.getMetric())
                .peerFileName(processedPeer.getPeerFileName())
                .peerData(multiModelPair)
                .build();
        Future<ProcessedPeer<Score>> bundleFuture = getExecutorService().submit(new AsyncScoreCalculator(peerToProcess, getScoreCalculator()));
        scoresCollector.add(bundleFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedPeer<Score>>> scoresCollector) {
        AsyncAllResultsProcessor asyncAllResultsProcessor = new AsyncAllResultsProcessor(scoresCollector, getConfiguration());
        getExecutorService().submit(asyncAllResultsProcessor);
    }
}
