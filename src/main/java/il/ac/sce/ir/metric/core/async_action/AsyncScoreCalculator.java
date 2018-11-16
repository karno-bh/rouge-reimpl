package il.ac.sce.ir.metric.core.async_action;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.score.ReportedProperties;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.ScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;

import java.util.concurrent.Callable;

public class AsyncScoreCalculator<X, Y extends ReportedProperties> implements Callable<ProcessedPeer<Y>> {

    private final ProcessedPeer<X> processedPeer;

    private final ScoreCalculator<X, Y> scoreCalculator;

    public AsyncScoreCalculator(ProcessedPeer<X> processedPeer, ScoreCalculator<X, Y> scoreCalculator) {
        this.processedPeer = processedPeer;
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public ProcessedPeer<Y> call() throws Exception {
        Y score = scoreCalculator.computeScore(processedPeer.getPeerData());
        return new ProcessedPeer.Builder<Y>()
                .processedCategory(processedPeer.getProcessedCategory())
                .processedSystem(processedPeer.getProcessedSystem())
                .metric(processedPeer.getMetric())
                .peerFileName(processedPeer.getPeerFileName())
                .peerData(score)
                .build();
    }
}
