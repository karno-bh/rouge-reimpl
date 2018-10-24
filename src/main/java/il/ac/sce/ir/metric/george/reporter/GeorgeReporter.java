package il.ac.sce.ir.metric.george.reporter;

import il.ac.sce.ir.metric.core.config.Configuration;
import il.ac.sce.ir.metric.core.config.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeorgeReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {




    }
}
