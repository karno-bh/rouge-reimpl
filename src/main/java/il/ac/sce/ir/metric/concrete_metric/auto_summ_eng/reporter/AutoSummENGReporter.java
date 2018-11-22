package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.reporter;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class AutoSummENGReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService executorService;

    private Configuration configuration;

    private TextProcessor<DocumentDesc, NGramSymWinDocument> nGramSymWinDocumentTextProcessor;

    private TextProcessor<DocumentDesc, SimpleTextDocument> simpleTextDocumentTextProcessor;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public TextProcessor<DocumentDesc, NGramSymWinDocument> getnGramSymWinDocumentTextProcessor() {
        return nGramSymWinDocumentTextProcessor;
    }

    public void setnGramSymWinDocumentTextProcessor(TextProcessor<DocumentDesc, NGramSymWinDocument> nGramSymWinDocumentTextProcessor) {
        this.nGramSymWinDocumentTextProcessor = nGramSymWinDocumentTextProcessor;
    }

    public TextProcessor<DocumentDesc, SimpleTextDocument> getSimpleTextDocumentTextProcessor() {
        return simpleTextDocumentTextProcessor;
    }

    public void setSimpleTextDocumentTextProcessor(TextProcessor<DocumentDesc, SimpleTextDocument> simpleTextDocumentTextProcessor) {
        this.simpleTextDocumentTextProcessor = simpleTextDocumentTextProcessor;
    }

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {

        final FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        String workingSetDirectory = getConfiguration().getWorkingSetDirectory();
        List<ProcessedSystem> processedSystems = fileSystemTopologyResolver.getProcessedSystems(workingSetDirectory, processedCategory);

        for (ProcessedSystem processedSystem : processedSystems) {
            List<String> peerFileNames = fileSystemTopologyResolver.getPeerFileNames(processedSystem);
            for (String peerFileName : peerFileNames) {
                File modelsDirectory = fileSystemTopologyResolver.getModelsDirectory(workingSetDirectory, processedCategory);
                List<Text<String>> modelTextsPerPeer = fileSystemTopologyResolver.getModelTextsPerPeer(modelsDirectory, peerFileName);
                for (Text<String> modelPerPeer : modelTextsPerPeer) {

                }
            }
        }

    }
}
