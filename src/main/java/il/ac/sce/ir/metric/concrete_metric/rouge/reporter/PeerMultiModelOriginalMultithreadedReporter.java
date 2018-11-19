package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.core.async_action.AsyncAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemCommons;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PeerMultiModelOriginalMultithreadedReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

    private ExecutorService executorService;

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

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    //    private boolean headerCreated = false;

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {
        Configuration configuration = getConfiguration();
        FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        FileSystemPath fileSystemPath = new FileSystemPath();
        FileSystemCommons fileSystemCommons = new FileSystemCommons();

        fileSystemCommons.requireAndCreateResultDirectory(configuration.getResultDirectory());
        String workingSetDirectory = configuration.getWorkingSetDirectory();

        File modelsDirectory = fileSystemTopologyResolver.getModelsDirectory(workingSetDirectory, processedCategory);

        List<ProcessedSystem> processedSystems = fileSystemTopologyResolver.getProcessedSystems(workingSetDirectory, processedCategory);
        if (processedSystems == null || processedSystems.isEmpty()) {
            logger.warn("Category {} does not have any system", processedCategory.getDirLocation());
            return;
        }

        List<Future<ProcessedPeer<Score>>> scoresFutures = new ArrayList<>();
        for (ProcessedSystem processedSystem : processedSystems) {

            String processedSystemDirLocation = processedSystem.getDirLocation();
            List<String> peerFileNames = fileSystemTopologyResolver.getPeerFileNames(processedSystem);
            if (peerFileNames.isEmpty()) {
                logger.warn("System {} does not have any file", processedSystem.getDescription());
                continue;
            }

            for (final String peerFileName : peerFileNames) {
                List<Text<String>> modelsPerPeer = fileSystemTopologyResolver.getModelTextsPerPeer(modelsDirectory, peerFileName);
                if (modelsPerPeer == null || modelsPerPeer.isEmpty()) {
                    logger.warn("No models found for peer {} in category {}", peerFileName, processedCategory.getDescription());
                    continue;
                }
                Text<String> peerText = Text.asFileLocation(fileSystemPath.combinePath(processedSystemDirLocation, peerFileName));
                MultiModelPair multiModelPair = new MultiModelPair(peerText, modelsPerPeer);

                ProcessedPeer<MultiModelPair> processedPeer = new ProcessedPeer.Builder<MultiModelPair>()
                        .processedCategory(processedCategory)
                        .processedSystem(processedSystem)
                        .metric(metric)
                        .peerFileName(peerFileName)
                        .peerData(multiModelPair)
                        .build();
                Future<ProcessedPeer<Score>> bundleFuture = executorService.submit(new AsyncScoreCalculator(processedPeer, getScoreCalculator()));
                scoresFutures.add(bundleFuture);
                // reportConcreteSystem(processedCategory, processedSystem, metric, peerFileName, score, headerCreated);
            }
        }

        // processAllResults(scoresFutures);
        AsyncAllResultsProcessor asyncAllResultsProcessor = new AsyncAllResultsProcessor(scoresFutures, getConfiguration(), null, null);
        executorService.submit(asyncAllResultsProcessor);
    }
}
