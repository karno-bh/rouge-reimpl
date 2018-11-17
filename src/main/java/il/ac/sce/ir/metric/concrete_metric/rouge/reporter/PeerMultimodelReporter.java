package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.core.async_action.AsyncAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;

import java.util.List;
import java.util.concurrent.ExecutionException;
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

        AsyncScoreCalculator<MultiModelPair, Score> task = new AsyncScoreCalculator<>(peerToProcess, getScoreCalculator());
        Future<ProcessedPeer<Score>> bundleFuture = getExecutorService().submit(task);
        scoresCollector.add(bundleFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedPeer<Score>>> scoresCollector) {
        AsyncAllResultsProcessor<Score> asyncAllResultsProcessor = new AsyncAllResultsProcessor<>(scoresCollector, getConfiguration());
//        getExecutorService().submit(asyncAllResultsProcessor);
        try {
            asyncAllResultsProcessor.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
