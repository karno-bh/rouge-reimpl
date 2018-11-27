package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score;

public class SimpleTextNGramResultPair {

    private final ComparisonResult simpleTextComparisonResult;

    private final ComparisonResult nGramComparisonResult;

    public ComparisonResult getSimpleTextComparisonResult() {
        return simpleTextComparisonResult;
    }

    public ComparisonResult getnGramComparisonResult() {
        return nGramComparisonResult;
    }

    public SimpleTextNGramResultPair(ComparisonResult simpleTextComparisonResult, ComparisonResult nGramComparisonResult) {
        this.simpleTextComparisonResult = simpleTextComparisonResult;
        this.nGramComparisonResult = nGramComparisonResult;
    }
}
