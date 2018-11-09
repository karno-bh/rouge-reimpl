package il.ac.sce.ir.metric.core.reporter;

import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;

public interface Reporter {

    void report(ProcessedCategory processedCategory, String metric);

}
