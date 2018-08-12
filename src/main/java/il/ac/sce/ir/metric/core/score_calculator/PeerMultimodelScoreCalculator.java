package il.ac.sce.ir.metric.core.score_calculator;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.score.Score;

import java.util.List;

public interface PeerMultimodelScoreCalculator extends ScoreCalculator<Score> {

    Text<String> getPeer();
    void setPeer(Text<String> peer);

    List<Text<String>> getModels();
    void setModels(List<Text<String>> models);



}
