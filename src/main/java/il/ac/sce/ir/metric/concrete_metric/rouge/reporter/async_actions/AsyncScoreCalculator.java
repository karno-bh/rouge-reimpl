package il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions;

import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions.data.AsyncScoreCalculatorInputData;
import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.data.ReportedBundle;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;

import java.util.concurrent.Callable;

public class AsyncScoreCalculator implements Callable<ReportedBundle> {

    private final AsyncScoreCalculatorInputData  asyncScoreCalculatorInputData;

    private final PeerMultimodelScoreCalculator scoreCalculator;

    public AsyncScoreCalculator(AsyncScoreCalculatorInputData asyncScoreCalculatorInputData, PeerMultimodelScoreCalculator scoreCalculator) {
        this.asyncScoreCalculatorInputData = asyncScoreCalculatorInputData;
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public ReportedBundle call() throws Exception {
        Score score = scoreCalculator.computeScore(asyncScoreCalculatorInputData.getMultiModelPair());
        return new ReportedBundle.Builder()
                .processedCategory(asyncScoreCalculatorInputData.getProcessedCategory())
                .processedSystem(asyncScoreCalculatorInputData.getProcessedSystem())
                .metric(asyncScoreCalculatorInputData.getMetric())
                .peerFileName(asyncScoreCalculatorInputData.getPeerFileName())
                .score(score)
                .build();
    }
}
