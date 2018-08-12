package il.ac.sce.ir.metric.core.config;

import il.ac.sce.ir.metric.core.score_calculator.ScoreCalculator;
import il.ac.sce.ir.metric.core.score.Score;

public class PeerMultimodelReportProcessor extends ReportProcessor {

    @Override
    public void process() {
        ScoreCalculator<Score> scoreCalculator = (ScoreCalculator<Score>) getScoreCalculator();
        Score score = scoreCalculator.computeScore();

    }
}
