package il.ac.sce.ir.metric.rouge.reporter;

import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score.Score;

import java.util.List;

public class RougeLMultimodelScoreCalculator implements PeerMultimodelScoreCalculator {

    private BiTextProcessor<List<String>, int[][]> dpMatrixProcessor;

    private TextProcessor<String, List<String>> tokensProcessor;

    private List<Text<String>> models;

    private Text<String> peer;

    public BiTextProcessor<List<String>, int[][]> getDpMatrixProcessor() {
        return dpMatrixProcessor;
    }

    public void setDpMatrixProcessor(BiTextProcessor<List<String>, int[][]> dpMatrixProcessor) {
        this.dpMatrixProcessor = dpMatrixProcessor;
    }

    public TextProcessor<String, List<String>> getTokensProcessor() {
        return tokensProcessor;
    }

    public void setTokensProcessor(TextProcessor<String, List<String>> tokensProcessor) {
        this.tokensProcessor = tokensProcessor;
    }

    @Override
    public List<Text<String>> getModels() {
        return models;
    }

    @Override
    public void setModels(List<Text<String>> models) {
        this.models = models;
    }

    @Override
    public Text<String> getPeer() {
        return peer;
    }

    @Override
    public void setPeer(Text<String> peer) {
        this.peer = peer;
    }

    @Override
    public Score computeScore() {
        double modelLengthCount = 0.0;
        double hitCount = 0.0;
        Text<List<String>> peerTokens = tokensProcessor.process(peer);
        for (Text<String> model : models) {
            Text<List<String>> modelTokens = tokensProcessor.process(model);
            modelLengthCount += modelTokens.getTextData().size();
            BiText<int[][]> dpMatrix = dpMatrixProcessor.process(peerTokens, modelTokens);
            int[][] dpMatrixRepr = dpMatrix.getData();
            int rows = dpMatrixRepr.length;
            int columns = dpMatrixRepr[0].length;
            int hits = dpMatrixRepr[rows - 1][columns - 1];
            hitCount += hits;
        }
        double presicion = hitCount / ((double)peerTokens.getTextData().size() * (double) models.size());
        double recall = hitCount / modelLengthCount;
        Score score = new Score();
        score.setPrecision(presicion);
        score.setRecall(recall);
        return score;
    }
}
