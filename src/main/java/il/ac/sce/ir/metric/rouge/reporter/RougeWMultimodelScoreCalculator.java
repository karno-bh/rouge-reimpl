package il.ac.sce.ir.metric.rouge.reporter;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.function.DoubleDoubleFunction;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score.Score;

import java.util.List;
import java.util.function.Supplier;

public class RougeWMultimodelScoreCalculator implements PeerMultimodelScoreCalculator {

    private BiTextProcessor<List<String>, boolean[]> someModelMatchProcessor;

    private TextProcessor<String, List<String>> tokenProcessor;

    private List<Text<String>> models;

    private Text<String> peer;

    private Supplier<DoubleDoubleFunction> weightFunctionSupplier;

    private Supplier<DoubleDoubleFunction> inverseWeightFunctionSupplier;

    public Supplier<DoubleDoubleFunction> getWeightFunctionSupplier() {
        return weightFunctionSupplier;
    }

    public void setWeightFunctionSupplier(Supplier<DoubleDoubleFunction> weightFunctionSupplier) {
        this.weightFunctionSupplier = weightFunctionSupplier;
    }

    public Supplier<DoubleDoubleFunction> getInverseWeightFunctionSupplier() {
        return inverseWeightFunctionSupplier;
    }

    public void setInverseWeightFunctionSupplier(Supplier<DoubleDoubleFunction> inverseWeightFunctionSupplier) {
        this.inverseWeightFunctionSupplier = inverseWeightFunctionSupplier;
    }


    public BiTextProcessor<List<String>, boolean[]> getSomeModelMatchProcessor() {
        return someModelMatchProcessor;
    }

    public void setSomeModelMatchProcessor(BiTextProcessor<List<String>, boolean[]> someModelMatchProcessor) {
        this.someModelMatchProcessor = someModelMatchProcessor;
    }

    public TextProcessor<String, List<String>> getTokenProcessor() {
        return tokenProcessor;
    }

    public void setTokenProcessor(TextProcessor<String, List<String>> tokenProcessor) {
        this.tokenProcessor = tokenProcessor;
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
        DoubleDoubleFunction weightFunction = weightFunctionSupplier.get();
        DoubleDoubleFunction inverseWeightFunction = inverseWeightFunctionSupplier.get();
        List<String> peerTokens = tokenProcessor.process(peer).getTextData();
        double totalHit = 0.0;
        double totalBase = 0.0;
        double totalPrecision = 0.0;
        for (Text<String> modelText : models) {
            List<String> modelTokens = tokenProcessor.process(modelText).getTextData();
            double base = weightFunction.apply(modelTokens.size());
            double hit = 0;
            double hitLen = 0;
            boolean[] someModelMatch = someModelMatchProcessor.process(tokenProcessor.process(peer), tokenProcessor.process(modelText)).getData();
            /*String s = "";
            for (int i = 0; i < someModelMatch.length; i++) {
               // String s = "";
                if (i != 0) {
                    s += ", ";
                }
                s += someModelMatch[i] ? "1" : "0";
            }
            System.out.println(s);*/
            for (int matchIndex = 0; matchIndex < modelTokens.size(); matchIndex++) {
                int nextMatchIndex = matchIndex + 1;
                if (someModelMatch[matchIndex]) {
                    hitLen++;
                    if ((nextMatchIndex < someModelMatch.length && !someModelMatch[nextMatchIndex]) ||
                            nextMatchIndex == someModelMatch.length) {
                        //System.out.println("hitLen: " + hitLen);
                        hit += weightFunction.apply(hitLen);
                        //System.out.println(("Hit: " + hit));
                        //println "hit $hit"
                        //println "hit: $hit"
                        hitLen = 0;
                    }
                }
            }
            totalHit += hit;
            totalBase += weightFunction.apply(base);
            totalPrecision += weightFunction.apply(peerTokens.size());
        }
        double recall = inverseWeightFunction.apply(totalHit / totalBase);
        double precision = inverseWeightFunction.apply(totalHit / totalPrecision);
        Score score = new Score();
        score.setRecall(recall);
        score.setPrecision(precision);
        return score;
    }
}
