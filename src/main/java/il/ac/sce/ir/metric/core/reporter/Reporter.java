package il.ac.sce.ir.metric.core.reporter;

import il.ac.sce.ir.metric.core.config.ProcessedCategory;

public interface Reporter {

    void report(ProcessedCategory processedCategory, String metric);

}
