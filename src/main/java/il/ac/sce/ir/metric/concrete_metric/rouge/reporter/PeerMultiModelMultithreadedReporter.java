package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerMultiModelMultithreadedReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

    private PeerMultimodelScoreCalculator scoreCalculator;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public PeerMultimodelScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(PeerMultimodelScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }


    @Override
    public void report(ProcessedCategory processedCategory, String metric) {

    }
}
