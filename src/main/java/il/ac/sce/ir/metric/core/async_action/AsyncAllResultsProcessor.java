package il.ac.sce.ir.metric.core.async_action;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.reporter.utils.CommonPeerFileReporter;
import il.ac.sce.ir.metric.core.score.ReportedProperties;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncAllResultsProcessor<T extends ReportedProperties> implements Callable<Void> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<Future<ProcessedPeer<T>>> allResultsPromisses;

    private final Arbiter arbiter;

    private final String metricName;

    private final Configuration configuration;

    public AsyncAllResultsProcessor(List<Future<ProcessedPeer<T>>> allResultsPromisses,
                                    Configuration configuration,
                                    Arbiter arbiter,
                                    String metricName) {
        Objects.requireNonNull(allResultsPromisses, "Result Promisses cannot be null");
        Objects.requireNonNull(configuration, "Configuration could not be null");
        this.allResultsPromisses = allResultsPromisses;
        this.configuration = configuration;
        this.arbiter = arbiter;
        this.metricName = metricName;
    }

    public List<Future<ProcessedPeer<T>>> getAllResultsPromisses() {
        return allResultsPromisses;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Arbiter getArbiter() {
        return arbiter;
    }

    public String getMetricName() {
        return metricName;
    }

    @Override
    public Void call() throws Exception {
        List<Future<ProcessedPeer<T>>> allResultsPromisses = getAllResultsPromisses();
        List<ProcessedPeer<T>> reportedBundles = new ArrayList<>();
        try {
            for (Future<ProcessedPeer<T>> reportedBundleFuture : allResultsPromisses) {
                try {
                    ProcessedPeer<T> reportedBundle = reportedBundleFuture.get();
                    reportedBundles.add(reportedBundle);
                } catch (InterruptedException e) {
                    logger.error("Main thread pool was interrupted", e);
                    throw new IllegalStateException("Main thread pool was interrupted", e);
                } catch (ExecutionException e) {
                    logger.error("Got exception while calculating ReportedBundle");
                    throw new RuntimeException("Got exception while calculating ReportedBundle", e);
                }
            }

            groupAndReport(reportedBundles);
            return null;
        } finally {
            if (getArbiter() != null) {
                getArbiter().signalizeFinish(getMetricName());
            }
        }
    }

    protected void groupAndReport(List<ProcessedPeer<T>> reportedBundles) {

        Map<String, List<ProcessedPeer<T>>> groups = new HashMap<>();

        MessageFormat keyFormat = new MessageFormat("{0}_{1}_{2}");
        for (final ProcessedPeer<T> reportedBundle : reportedBundles) {
            String key = keyFormat.format(new Object[]{
                    reportedBundle.getProcessedCategory().getDirLocation(),
                    reportedBundle.getProcessedSystem().getDirLocation(),
                    reportedBundle.getMetric()
            });
            List<ProcessedPeer<T>> bundlesPerFile = groups.computeIfAbsent(key, _key -> new ArrayList<>());
            bundlesPerFile.add(reportedBundle);
        }
        dumpAllFiles(groups);
    }

    protected void dumpAllFiles(Map<String, List<ProcessedPeer<T>>> groups) {
        Configuration configuration = getConfiguration();
        CommonPeerFileReporter commonPeerFileReporter = new CommonPeerFileReporter();
        groups.forEach(
            (groupKey, reportedBundlesPerFile) -> {
                commonPeerFileReporter.reportConcreteFile(reportedBundlesPerFile, configuration.getResultDirectory());
            }
        );

    }

}
