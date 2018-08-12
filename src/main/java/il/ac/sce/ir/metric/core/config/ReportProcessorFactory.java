package il.ac.sce.ir.metric.core.config;

public interface ReportProcessorFactory {

    ReportProcessor resolve(String metricName);

}
