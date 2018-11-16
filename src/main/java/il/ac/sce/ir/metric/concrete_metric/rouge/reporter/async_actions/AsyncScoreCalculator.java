package il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;

import java.util.concurrent.Callable;

public class AsyncScoreCalculator implements Callable<ProcessedPeer<Score>> {

    private final ProcessedPeer<MultiModelPair> processedPeer;

    private final PeerMultimodelScoreCalculator scoreCalculator;

    public AsyncScoreCalculator(ProcessedPeer<MultiModelPair> processedPeer, PeerMultimodelScoreCalculator scoreCalculator) {
        this.processedPeer = processedPeer;
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public ProcessedPeer<Score> call() throws Exception {
        Score score = scoreCalculator.computeScore(processedPeer.getPeerData());
        return new ProcessedPeer.Builder<Score>()
                .processedCategory(processedPeer.getProcessedCategory())
                .processedSystem(processedPeer.getProcessedSystem())
                .metric(processedPeer.getMetric())
                .peerFileName(processedPeer.getPeerFileName())
                .peerData(score)
                .build();
    }
}
