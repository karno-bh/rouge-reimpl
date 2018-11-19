package il.ac.sce.ir.metric.core.reporter.template;

import il.ac.sce.ir.metric.core.async_action.Arbiter;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemCommons;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class AbstractPeerReporter<T> implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileSystemPath fileSystemPath = new FileSystemPath();

    private final FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();

    private Configuration configuration;

    private ExecutorService executorService;

    private volatile File modelsDirectory;

    private Arbiter arbiter;

    private String metricName;

    public Logger getLogger() {
        return logger;
    }

    public FileSystemPath getFileSystemPath() {
        return fileSystemPath;
    }

    public FileSystemTopologyResolver getFileSystemTopologyResolver() {
        return fileSystemTopologyResolver;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public File getModelsDirectory() {
        return modelsDirectory;
    }

    public void setModelsDirectory(File modelsDirectory) {
        this.modelsDirectory = modelsDirectory;
    }

    public Arbiter getArbiter() {
        return arbiter;
    }

    public void setArbiter(Arbiter arbiter) {
        this.arbiter = arbiter;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {
        Configuration configuration = getConfiguration();
        FileSystemCommons fileSystemCommons = new FileSystemCommons();

        fileSystemCommons.requireAndCreateResultDirectory(configuration.getResultDirectory());
        String workingSetDirectory = configuration.getWorkingSetDirectory();

        File modelsDirectory = fileSystemTopologyResolver.getModelsDirectory(workingSetDirectory, processedCategory);
        setModelsDirectory(modelsDirectory);

        List<ProcessedSystem> processedSystems = fileSystemTopologyResolver.getProcessedSystems(workingSetDirectory, processedCategory);
        if (processedSystems == null || processedSystems.isEmpty()) {
            logger.warn("Category {} does not have any system", processedCategory.getDirLocation());
            return;
        }

        List<T> scoresCollector = new ArrayList<>();
        for (ProcessedSystem processedSystem : processedSystems) {

            List<String> peerFileNames = fileSystemTopologyResolver.getPeerFileNames(processedSystem);
            if (peerFileNames.isEmpty()) {
                logger.warn("System {} does not have any file", processedSystem.getDescription());
                continue;
            }

            for (final String peerFileName : peerFileNames) {
                ProcessedPeer<Void> processedPeer = new ProcessedPeer.Builder<Void>()
                        .processedCategory(processedCategory)
                        .processedSystem(processedSystem)
                        .metric(metric)
                        .peerFileName(peerFileName)
                        .checkPeerData(false)
                        .build();
                processConcretePeer(processedPeer, scoresCollector);
            }
        }

        processResults(scoresCollector);
    }

    protected abstract void processConcretePeer(ProcessedPeer<Void> processedPeer, List<T> scoresCollector);

    protected abstract void processResults(List<T> scoresCollector);
}
