package il.ac.sce.ir.metric.concrete_metric.rouge.score;

import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score.Score;

import java.util.List;
import java.util.Map;

public class RougeNMultimodelScoreCalculator implements PeerMultimodelScoreCalculator {

    private BiTextProcessor<String, Integer> nGramHits;

    private TextProcessor<String, Map<String, Integer>> nGramProcessor;

    private List<Text<String>> models;

    private Text<String> peer;

    public void setnGramHits(BiTextProcessor<String, Integer> nGramHits) {
        this.nGramHits = nGramHits;
    }

    public void setnGramProcessor(TextProcessor<String, Map<String, Integer>> nGramProcessor) {
        this.nGramProcessor = nGramProcessor;
    }

    @Override
    public void setModels(List<Text<String>> models) {
        this.models = models;
    }

    @Override
    public void setPeer(Text<String> peer) {
        this.peer = peer;
    }

    @Override
    public List<Text<String>> getModels() {
        return models;
    }

    @Override
    public Text<String> getPeer() {
        return peer;
    }

    public BiTextProcessor<String, Integer> getnGramHits() {
        return nGramHits;
    }

    @Override
    public Score computeScore() {
        double totalHits = 0;
        double totalModelGrams = 0;
        double totalPeerGrams = 0;
        for (Text<String> model : models) {
            Text<Map<String, Integer>> modelGrams = nGramProcessor.process(model);
            Text<Map<String, Integer>> peerGrams = nGramProcessor.process(peer);
            BiText<Integer> hits = nGramHits.process(model, peer);
            totalHits += hits.getData();
            totalModelGrams += modelGrams.getTextData().get("_cn_");
            totalPeerGrams += peerGrams.getTextData().get("_cn_");
        }
        Score score = new Score();
        score.setPrecision(totalHits / totalPeerGrams);
        score.setRecall(totalHits / totalModelGrams);
        return score;
    }
}
