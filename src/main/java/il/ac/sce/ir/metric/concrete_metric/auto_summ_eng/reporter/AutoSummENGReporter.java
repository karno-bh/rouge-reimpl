package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.reporter;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.async_action.AsyncAllResultsProcessor;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.*;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.SimpleTextNGramResultPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator.AutoSummENGScoreCalculator;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.sync.Arbiter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AutoSummENGReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService executorService;

    private Arbiter arbiter;

    private Configuration configuration;

    private AutoSummENGScoreCalculator nGramSymWinDocumentTextProcessor;

    private AutoSummENGScoreCalculator simpleTextDocumentTextProcessor;

    private SimpleTextConfig simpleTextConfig;

    private NGramTextConfig nGramTextConfig;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Arbiter getArbiter() {
        return arbiter;
    }

    public void setArbiter(Arbiter arbiter) {
        this.arbiter = arbiter;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public AutoSummENGScoreCalculator getnGramSymWinDocumentTextProcessor() {
        return nGramSymWinDocumentTextProcessor;
    }

    public void setnGramSymWinDocumentTextProcessor(AutoSummENGScoreCalculator nGramSymWinDocumentTextProcessor) {
        this.nGramSymWinDocumentTextProcessor = nGramSymWinDocumentTextProcessor;
    }

    public AutoSummENGScoreCalculator getSimpleTextDocumentTextProcessor() {
        return simpleTextDocumentTextProcessor;
    }

    public void setSimpleTextDocumentTextProcessor(AutoSummENGScoreCalculator simpleTextDocumentTextProcessor) {
        this.simpleTextDocumentTextProcessor = simpleTextDocumentTextProcessor;
    }

    public SimpleTextConfig getSimpleTextConfig() {
        return simpleTextConfig;
    }

    public void setSimpleTextConfig(SimpleTextConfig simpleTextConfig) {
        this.simpleTextConfig = simpleTextConfig;
    }

    public NGramTextConfig getnGramTextConfig() {
        return nGramTextConfig;
    }

    public void setnGramTextConfig(NGramTextConfig nGramTextConfig) {
        this.nGramTextConfig = nGramTextConfig;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {

        final FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        final FileSystemPath fileSystemPath = new FileSystemPath();
        String workingSetDirectory = getConfiguration().getWorkingSetDirectory();
        List<ProcessedSystem> processedSystems = fileSystemTopologyResolver.getProcessedSystems(workingSetDirectory, processedCategory);

        List<Future<ProcessedChunk<SimpleTextNGramResultPair>>> promises = new ArrayList<>();
        for (ProcessedSystem processedSystem : processedSystems) {
            List<String> peerFileNames = fileSystemTopologyResolver.getPeerFileNames(processedSystem);
            for (String peerFileName : peerFileNames) {
                File modelsDirectory = fileSystemTopologyResolver.getModelsDirectory(workingSetDirectory, processedCategory);
                List<Text<String>> modelTextsPerPeer = fileSystemTopologyResolver.getModelTextsPerPeer(modelsDirectory, peerFileName);
                String absolutePeerFileName = fileSystemPath.combinePath(processedSystem.getDirLocation(), peerFileName);
                for (Text<String> modelPerPeer : modelTextsPerPeer) {
                    SimpleTextConfig simpleTextConfig = getSimpleTextConfig();
                    NGramTextConfig nGramTextConfig = getnGramTextConfig();
                    PeerSingleModelPair simpleTextPeerSingleModelPair = null;
                    PeerSingleModelPair nGramPeerSingleModelPair = null;
                    if (simpleTextConfig != null) {
                        DocumentDesc peerDocumentDesc = new DocumentDesc(absolutePeerFileName, simpleTextConfig.getWordMin(),
                                simpleTextConfig.getWordMax(), simpleTextConfig.getWordDist());
                        DocumentDesc modelDocumentDesc = new DocumentDesc(modelPerPeer.getTextData(), simpleTextConfig.getWordMin(),
                                simpleTextConfig.getWordMax(), simpleTextConfig.getWordDist());
                        simpleTextPeerSingleModelPair  = new PeerSingleModelPair(peerDocumentDesc, modelDocumentDesc);
                    }
                    if (nGramTextConfig != null) {
                        DocumentDesc peerDocumentDesc = new DocumentDesc(absolutePeerFileName, nGramTextConfig.getCharMin(),
                                nGramTextConfig.getCharMax(), nGramTextConfig.getCharDist());
                        DocumentDesc modelDocumentDesc = new DocumentDesc(modelPerPeer.getTextData(), nGramTextConfig.getCharMin(),
                                nGramTextConfig.getCharMax(), nGramTextConfig.getCharDist());
                        nGramPeerSingleModelPair  = new PeerSingleModelPair(peerDocumentDesc, modelDocumentDesc);
                    }
                    SimpleTextNGramPair simpleTextNGramPair = new SimpleTextNGramPair(simpleTextPeerSingleModelPair, nGramPeerSingleModelPair);

                    ProcessedChunk<SimpleTextNGramPair> processedChunk = new ProcessedChunk.Builder<SimpleTextNGramPair>()
                            .processedSystem(processedSystem)
                            .processedCategory(processedCategory)
                            .metric(metric)
                            .peerFileName(peerFileName)
                            .model(modelPerPeer.getTextData())
                            .chunkData(simpleTextNGramPair)
                            .build();

                    AsyncScoreCalculator asyncScoreCalculator = new AsyncScoreCalculator(processedChunk,
                            getSimpleTextDocumentTextProcessor(),
                            getnGramSymWinDocumentTextProcessor());

                    Future<ProcessedChunk<SimpleTextNGramResultPair>> promise = getExecutorService().submit(asyncScoreCalculator);
                    promises.add(promise);
                }
            }
        }

        AsyncAllResultsProcessor asyncAllResultsProcessor = new AsyncAllResultsProcessor.Builder()
                .promises(promises)
                .arbiter(getArbiter())
                .metric(metric)
                .resultDirectory(getConfiguration().getResultDirectory())
                .build();

        getExecutorService().submit(asyncAllResultsProcessor);
    }
}
