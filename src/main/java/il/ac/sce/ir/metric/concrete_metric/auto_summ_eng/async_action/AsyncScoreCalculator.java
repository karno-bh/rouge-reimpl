package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.async_action;

import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.PeerSingleModelPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.SimpleTextNGramPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.ComparisonResult;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.SimpleTextNGramResultPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator.AutoSummENGScoreCalculator;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;

import java.util.concurrent.Callable;

public class AsyncScoreCalculator implements Callable<ProcessedChunk<SimpleTextNGramResultPair>>{

    private final ProcessedChunk<SimpleTextNGramPair> processedChunk;

    private final AutoSummENGScoreCalculator simpleTextScoreCalculator;

    private final AutoSummENGScoreCalculator nGramScoreCalculator;

    public AsyncScoreCalculator(ProcessedChunk<SimpleTextNGramPair> processedChunk, AutoSummENGScoreCalculator simpleTextScoreCalculator, AutoSummENGScoreCalculator nGramScoreCalculator) {
        this.processedChunk = processedChunk;
        this.simpleTextScoreCalculator = simpleTextScoreCalculator;
        this.nGramScoreCalculator = nGramScoreCalculator;
    }

    @Override
    public ProcessedChunk<SimpleTextNGramResultPair> call() throws Exception {
        SimpleTextNGramPair simpleTextNGramPair = processedChunk.getChunkData();
        PeerSingleModelPair simpleTextPeerSingleModelPair = simpleTextNGramPair.getSimpleTextPair();
        PeerSingleModelPair nGramPeerSingleModelPair = simpleTextNGramPair.getSimpleTextPair();

        ComparisonResult simpleTextResult = null;
        if (simpleTextPeerSingleModelPair != null) {
            simpleTextResult = simpleTextScoreCalculator.computeScore(simpleTextPeerSingleModelPair);
        }

        ComparisonResult nGramResult = null;
        if (nGramPeerSingleModelPair != null) {
            nGramResult = nGramScoreCalculator.computeScore(nGramPeerSingleModelPair);
        }

        SimpleTextNGramResultPair result = new SimpleTextNGramResultPair(simpleTextResult, nGramResult);
        ProcessedChunk<SimpleTextNGramResultPair> resultChunk = new ProcessedChunk.Builder<SimpleTextNGramResultPair>()
                .cloneWithoutData(processedChunk)
                .chunkData(result)
                .build();

        return resultChunk;
    }
}
