package il.ac.sce.ir.metric.core.async_action;

import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.utils.CommonChunkFileReporter;
import il.ac.sce.ir.metric.core.score.ReportedProperties;
import il.ac.sce.ir.metric.core.sync.Arbiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class AsyncPeerAllResultsProcessor<T extends ReportedProperties> implements Callable<Void> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<Future<ProcessedChunk<T>>> allResultsPromisses;

    private final Arbiter arbiter;

    private final String metricName;

    private final Configuration configuration;

    public AsyncPeerAllResultsProcessor(List<Future<ProcessedChunk<T>>> allResultsPromisses,
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

    public List<Future<ProcessedChunk<T>>> getAllResultsPromisses() {
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
        List<Future<ProcessedChunk<T>>> allResultsPromisses = getAllResultsPromisses();
        if (allResultsPromisses.isEmpty()) {
            return null;
        }
        List<ProcessedChunk<T>> reportedBundles = new ArrayList<>();
        try {
            for (Future<ProcessedChunk<T>> reportedBundleFuture : allResultsPromisses) {
                try {
                    ProcessedChunk<T> reportedBundle = reportedBundleFuture.get();
                    reportedBundles.add(reportedBundle);
                } catch (InterruptedException e) {
                    logger.error("Main thread pool was interrupted", e);
                    throw new IllegalStateException("Main thread pool was interrupted", e);
                } catch (ExecutionException e) {
                    logger.error("Got exception while calculating ReportedBundle", e);
                    throw new RuntimeException("Got exception while calculating ReportedBundle", e);
                } catch (Exception e) {
                    logger.error("Got exception while calculating ReportedBundle", e);
                }
            }

            groupAndReport(reportedBundles);
            return null;
        } catch (Exception e) {
            logger.error("Error while processing peers", e);
            return null;
        } finally {
            if (getArbiter() != null) {
                getArbiter().signalizeFinish(getMetricName());
            }
        }
    }

    public void groupAndReport(List<ProcessedChunk<T>> reportedBundles) {
        ChunkTypeFunctionsFactory groupers = new ChunkTypeFunctionsFactory();
        Function<ProcessedChunk<T>, String> fileNameCombiner = groupers.getFileNameCombiner(reportedBundles.get(0));

        Map<String, List<ProcessedChunk<T>>> groups = new HashMap<>();

        // MessageFormat keyFormat = new MessageFormat("{0}_{1}_{2}");
        for (final ProcessedChunk<T> reportedBundle : reportedBundles) {
            String key = fileNameCombiner.apply(reportedBundle);
            List<ProcessedChunk<T>> bundlesPerFile = groups.computeIfAbsent(key, _key -> new ArrayList<>());
            bundlesPerFile.add(reportedBundle);
        }
        dumpAllFiles(groups);
    }

    public void dumpAllFiles(Map<String, List<ProcessedChunk<T>>> groups) {
        Configuration configuration = getConfiguration();
        CommonChunkFileReporter commonChunkFileReporter = new CommonChunkFileReporter();
        groups.forEach(
            (groupKey, reportedBundlesPerFile) -> {
                commonChunkFileReporter.reportConcreteFile(reportedBundlesPerFile, configuration.getResultDirectory());
            }
        );

    }

}
