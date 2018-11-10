package il.ac.sce.ir.metric.core.score_calculator;

public interface ScoreCalculator<X, Y> {

    Y computeScore(X x);

}
