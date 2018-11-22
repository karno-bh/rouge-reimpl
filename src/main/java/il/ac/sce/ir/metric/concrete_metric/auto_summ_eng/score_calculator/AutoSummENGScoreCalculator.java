package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator;

import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.PeerSingleModelPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.ComparisonResult;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.score_calculator.ScoreCalculator;

public interface AutoSummENGScoreCalculator extends ScoreCalculator<PeerSingleModelPair, ComparisonResult> {

}
