package il.ac.sce.ir.metric.concrete_metric.george.reporter;

import entity_extractor.TextEntities;
import gr.demokritos.iit.conceptualIndex.structs.Distribution;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.concrete_metric.george.data.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.tf_idf.DocumentOptimizedParser;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class GeorgeReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

    private Methods methods;

    private boolean keepTopTerms;

    private TextProcessor<String, TextEntities> entitiesTextProcessor;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Methods getMethods() {
        return methods;
    }

    public void setMethods(Methods methods) {
        this.methods = methods;

        keepTopTerms = methods.isEnabled(Methods.PLACEHOLDER) || methods.isEnabled(Methods.COSINE) || methods.isEnabled(Methods.PLACE_HOLDER_EXTRAWEIGHT);
    }

    public TextProcessor<String, TextEntities> getEntitiesTextProcessor() {
        return entitiesTextProcessor;
    }

    public void setEntitiesTextProcessor(TextProcessor<String, TextEntities> entitiesTextProcessor) {
        this.entitiesTextProcessor = entitiesTextProcessor;
    }

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {

        FileSystemPath path = new FileSystemPath();
        String absoluteCategoryTopicPath = path.combinePath(
                configuration.getWorkingSetDirectory(),
                processedCategory.getDirLocation(),
                Constants.TOPICS
        );
        logger.info("George Graph metrics starting prcessig topics under: {}", absoluteCategoryTopicPath);

        File topics = new File(absoluteCategoryTopicPath);
        if (!topics.isDirectory()) {
            throw new RuntimeException("Topic directory: " + absoluteCategoryTopicPath + " is not a directory");
        }

        String[] topicsFiles = topics.list((dir, name) -> {
            File file = new File(path.combinePath(dir.getAbsolutePath(), name));
            return file.isFile() && !file.isDirectory();
        });

        // DEBUG PRINT
        // Arrays.stream(topicsFiles).forEach(System.out::println);

        List<TextEntities> allTopicEntities = Arrays
                .stream(topicsFiles)
                .map((topicFile) -> path.combinePath(absoluteCategoryTopicPath, topicFile))
                .map(topicFile -> {
                    logger.info("Processing topic: '{}' for Named Entities", topicFile);
                    Text<String> initialText = Text.asFileLocation(topicFile);
                    Text<TextEntities> entitiesText = entitiesTextProcessor.process(initialText);
                    return entitiesText.getTextData();
                })
                .collect(Collectors.toList());

        logger.info("Calculating TF-IDF...");

        DocumentOptimizedParser dp = new DocumentOptimizedParser();
        if (keepTopTerms) {
            dp.parseFiles(allTopicEntities);
        }

        // For Cosine Similarity, create text distrubutions containing all terms
        Map<String, double[]> fullDistributions = null;
        if (methods.isEnabled(Methods.COSINE)) {
            Set<String> allTerms = dp.getAllTerms();
            ArrayList<String> allTermsList = new ArrayList<>();
            allTermsList.addAll(allTerms);
            fullDistributions = new HashMap<>();

            for (TextEntities text : allTopicEntities) {
                double[] fullTextDistrib = new double[allTermsList.size()];
                Distribution<String> textDistrib = dp.getDistributionOfDocument(text.getTitle());

                int count = 0;
                for (String term : allTerms) {
                    // If the text contains this term use its value, else set it to 0
                    if (textDistrib.asTreeMap().containsKey(term)) {
                        fullTextDistrib[count] = textDistrib.getValue(term);
                    } else {
                        fullTextDistrib[count] = 0.0;
                    }

                    count++;
                }

                fullDistributions.put(text.getTitle(), fullTextDistrib);
            }
        }

        System.out.println("Full Distributions: \n" + fullDistributions);

    }
}
