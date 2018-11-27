package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.util;

import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.ComparisonResult;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.SimpleTextNGramResultPair;
import il.ac.sce.ir.metric.core.config.Constants;

import java.util.*;

public class ProcessedResultMapper {

    public Collection<String> getGeneralHeader() {
        return Arrays.asList(
                Constants.CATEGORY,
                Constants.SYSTEM,
                Constants.PEER,
                Constants.MODEL
        );
    }

    public Collection<String> getScoreHeader() {
        return Arrays.asList(
                Constants.AUTO_SUMM_ENG_GRAPH_COOCCURENCE,
                Constants.AUTO_SUMM_ENG_GRAPH_VALUE,
                Constants.AUTO_SUMM_ENG_GRAPH_SIZE,

                Constants.AUTO_SUMM_ENG_HISTO_CONTAINMENT_SIMILARITY,
                Constants.AUTO_SUMM_ENG_HISTO_VALUE,
                Constants.AUTO_SUMM_ENG_HISTO_SIZE,

                Constants.AUTO_SUMM_ENG_HISTO_OVERALL_SIMIL,


                Constants.AUTO_SUMM_ENG_CHAR_GRAPH_COOCCURENCE,
                Constants.AUTO_SUMM_ENG_CHAR_GRAPH_VALUE,
                Constants.AUTO_SUMM_ENG_CHAR_GRAPH_SIZE,

                Constants.AUTO_SUMM_ENG_N_HISTO_CONTAINMENT_SIMILARITY,
                Constants.AUTO_SUMM_ENG_N_HISTO_VALUE,
                Constants.AUTO_SUMM_ENG_N_HISTO_SIZE,

                Constants.AUTO_SUMM_ENG_N_HISTO_OVERALL_SIMIL
        );
    }

    public Collection<String> getHeader() {
        List<String> result = new ArrayList<>();
        result.addAll(getGeneralHeader());
        result.addAll(getScoreHeader());
        return result;
    }

    public Map<String, Double> asMap(SimpleTextNGramResultPair pair) {
        Map<String, Double> result = new HashMap<>();
        if (pair == null) {
            return result;
        }
        ComparisonResult simpleTextComparisonResult = pair.getSimpleTextComparisonResult();
        if (simpleTextComparisonResult != null) {
            result.put(Constants.AUTO_SUMM_ENG_GRAPH_COOCCURENCE, simpleTextComparisonResult.getGraph().ContainmentSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_GRAPH_VALUE, simpleTextComparisonResult.getGraph().ValueSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_GRAPH_SIZE, simpleTextComparisonResult.getGraph().SizeSimilarity);

            result.put(Constants.AUTO_SUMM_ENG_HISTO_CONTAINMENT_SIMILARITY, simpleTextComparisonResult.getHistogram().ContainmentSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_HISTO_VALUE, simpleTextComparisonResult.getHistogram().ValueSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_HISTO_SIZE, simpleTextComparisonResult.getHistogram().SizeSimilarity);

            result.put(Constants.AUTO_SUMM_ENG_HISTO_OVERALL_SIMIL, simpleTextComparisonResult.getOverall().getOverallSimilarity());
        }

        ComparisonResult nGramComparisonResult = pair.getnGramComparisonResult();
        if (nGramComparisonResult != null) {
            result.put(Constants.AUTO_SUMM_ENG_CHAR_GRAPH_COOCCURENCE, nGramComparisonResult.getGraph().ContainmentSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_CHAR_GRAPH_VALUE, nGramComparisonResult.getGraph().ValueSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_CHAR_GRAPH_SIZE, nGramComparisonResult.getGraph().SizeSimilarity);

            result.put(Constants.AUTO_SUMM_ENG_N_HISTO_CONTAINMENT_SIMILARITY, nGramComparisonResult.getHistogram().ContainmentSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_N_HISTO_VALUE, nGramComparisonResult.getHistogram().ValueSimilarity);
            result.put(Constants.AUTO_SUMM_ENG_N_HISTO_SIZE, nGramComparisonResult.getHistogram().SizeSimilarity);

            result.put(Constants.AUTO_SUMM_ENG_N_HISTO_OVERALL_SIMIL, nGramComparisonResult.getOverall().getOverallSimilarity());
        }

        return  result;
    }
}
