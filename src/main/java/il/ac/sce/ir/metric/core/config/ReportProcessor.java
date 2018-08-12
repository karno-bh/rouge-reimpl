package il.ac.sce.ir.metric.core.config;

import il.ac.sce.ir.metric.core.score_calculator.ScoreCalculator;

public abstract class ReportProcessor {

    private String metricName;
    private ProcessedCategory processedCategory;
    private ScoreCalculator<?> scoreCalculator;
    private Container container;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public ProcessedCategory getProcessedCategory() {
        return processedCategory;
    }

    public void setProcessedCategory(ProcessedCategory processedCategory) {
        this.processedCategory = processedCategory;
    }

    public ScoreCalculator<?> getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(ScoreCalculator<?> scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public abstract void process();
}
