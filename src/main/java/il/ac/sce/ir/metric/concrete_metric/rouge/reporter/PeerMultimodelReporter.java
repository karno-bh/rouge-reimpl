package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.core.async_action.AllResultProcessorGroupers;
import il.ac.sce.ir.metric.core.async_action.Arbiter;
import il.ac.sce.ir.metric.core.async_action.AsyncPeerAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;

import java.util.List;
import java.util.concurrent.Future;

public class PeerMultimodelReporter extends AbstractPeerReporter<Future<ProcessedChunk<Score>>> {

    private PeerMultimodelScoreCalculator scoreCalculator;

    public PeerMultimodelScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(PeerMultimodelScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    protected void processConcretePeer(ProcessedChunk<Void> processedChunk, List<Future<ProcessedChunk<Score>>> scoresCollector) {

        List<Text<String>> modelsPerPeer = getFileSystemTopologyResolver().getModelTextsPerPeer(getModelsDirectory(), processedChunk.getPeerFileName());
        if (modelsPerPeer == null || modelsPerPeer.isEmpty()) {
            getLogger().warn("No models found for peer {} in category {}", processedChunk.getPeerFileName(), processedChunk.getProcessedCategory().getDescription());
            return;
        }

        Text<String> peerText = Text.asFileLocation(getFileSystemPath().combinePath(processedChunk.getProcessedSystem().getDirLocation(),
                processedChunk.getPeerFileName()));
        MultiModelPair multiModelPair = new MultiModelPair(peerText, modelsPerPeer);

        ProcessedChunk<MultiModelPair> peerToProcess = new ProcessedChunk.Builder<MultiModelPair>()
                .cloneWithoutData(processedChunk)
                .chunkData(multiModelPair)
                .build();

        AsyncScoreCalculator<MultiModelPair, Score> task = new AsyncScoreCalculator<>(peerToProcess, getScoreCalculator());
        Future<ProcessedChunk<Score>> bundleFuture = getExecutorService().submit(task);
        scoresCollector.add(bundleFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedChunk<Score>>> scoresCollector) {
        AllResultProcessorGroupers allResultProcessorGroupers = new AllResultProcessorGroupers();
        AsyncPeerAllResultsProcessor<Score> asyncPeerAllResultsProcessor = new AsyncPeerAllResultsProcessor<>(scoresCollector,
                getConfiguration(), getArbiter(), getMetricName(), allResultProcessorGroupers.getFileNamePeerCombiner(Score.class));
        getExecutorService().submit(asyncPeerAllResultsProcessor);
        /*try {
            asyncPeerAllResultsProcessor.call();
        } catch (Exception e) {
            getLogger().error("Error while dumping results onto the disk");
        }*/
    }
}
