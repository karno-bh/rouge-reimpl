package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.core.async_action.AsyncPeerAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.PeerMultiModelPair;

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
        PeerMultiModelPair peerMultiModelPair = new PeerMultiModelPair(peerText, modelsPerPeer);

        ProcessedChunk<PeerMultiModelPair> peerToProcess = new ProcessedChunk.Builder<PeerMultiModelPair>()
                .cloneWithoutData(processedChunk)
                .chunkData(peerMultiModelPair)
                .build();

        AsyncScoreCalculator<PeerMultiModelPair, Score> task = new AsyncScoreCalculator<>(peerToProcess, getScoreCalculator());
        Future<ProcessedChunk<Score>> bundleFuture = getExecutorService().submit(task);
        scoresCollector.add(bundleFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedChunk<Score>>> scoresCollector) {

        AsyncPeerAllResultsProcessor<Score> asyncPeerAllResultsProcessor = new AsyncPeerAllResultsProcessor<>(scoresCollector,
                getConfiguration(), getArbiter(), getMetricName());
        getExecutorService().submit(asyncPeerAllResultsProcessor);
        /*try {
            asyncPeerAllResultsProcessor.call();
        } catch (Exception e) {
            getLogger().error("Error while dumping results onto the disk");
        }*/
    }
}
