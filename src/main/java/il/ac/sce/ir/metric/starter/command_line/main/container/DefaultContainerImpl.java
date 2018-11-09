package il.ac.sce.ir.metric.starter.command_line.main.container;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import il.ac.sce.ir.metric.core.builder.BiTextPipeline;
import il.ac.sce.ir.metric.core.builder.BiTextPipelineExtractor;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.container.Container;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.container.data.ContainerConfigData;
import il.ac.sce.ir.metric.core.processor.*;
import il.ac.sce.ir.metric.core.recollector.CachedMapRecollector;
import il.ac.sce.ir.metric.core.reducer.*;
import il.ac.sce.ir.metric.concrete_metric.elena.reporter.ElenaReadbilityPeersReporter;
import il.ac.sce.ir.metric.concrete_metric.elena.reporter.ElenaReadbilityTopicsReporter;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.common.nlp.processor.CoreNLPTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.DPMatrixBiTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.NGramHits;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.NGramTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.SomeModelMatchContinuation;
import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.PeerMultimodelReporter;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.RougeLMultimodelScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.RougeNMultimodelScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.RougeWMultimodelScoreCalculator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultContainerImpl extends Container {

    private Configuration configuration;

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void build() {

        TextPipelineExtractor<String, List<String>> tokensExtractor = new TextPipelineExtractor<>();

        List<String> rougeNMetrics =
                new HashSet<>(configuration.getRequiredMetrics())
                        .stream()
                        .filter((metric) -> metric.trim().toLowerCase().startsWith(Constants.ROUGEN_LOWER_CASE))
                        .collect(Collectors.toList());
        List<Integer> rougeNParams =
                rougeNMetrics.stream()
                    .map(rougeNMetric -> {
                        String rougeNIndexStr = rougeNMetric.substring(Constants.ROUGEN_LOWER_CASE.length());
                        return Integer.parseInt(rougeNIndexStr);
                    })
                    .collect(Collectors.toList());

        TextPipeline<String, List<String>> initialPipeline = new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(tokensExtractor);
        Map<Integer, TextPipelineExtractor<String, Map<String, Integer>>> rougeNPipelineExtractors = new HashMap<>();
        Map<Integer, NGramHits> nGramHitsMap = new HashMap<>();

        for (int rougeNParam : rougeNParams) {
            TextPipelineExtractor<String, Map<String, Integer>> rougeNGramXExtractor = new TextPipelineExtractor<>();
            initialPipeline.pipe(new NGramTextProcessor(rougeNParam))
                    .cacheIn(CacheMemoryTextProcessor::new)
                    .extract(rougeNGramXExtractor);
            rougeNPipelineExtractors.put(rougeNParam, rougeNGramXExtractor);
            NGramHits nGramHits = new NGramHits(rougeNGramXExtractor.getTextProcessor());
            nGramHitsMap.put(rougeNParam, nGramHits);
        }


        for (String rougeNMetric : rougeNMetrics) {
            int rougeNIndex = Integer.parseInt(rougeNMetric.substring(Constants.ROUGEN_LOWER_CASE.length()));
            RougeNMultimodelScoreCalculator rougeNMultimodelScoreCalculator = new RougeNMultimodelScoreCalculator();
            rougeNMultimodelScoreCalculator.setnGramHits(nGramHitsMap.get(rougeNIndex));
            rougeNMultimodelScoreCalculator.setnGramProcessor(rougeNPipelineExtractors.get(rougeNIndex).getTextProcessor());

            PeerMultimodelReporter reporter = new PeerMultimodelReporter();
            reporter.setScoreCalculator(rougeNMultimodelScoreCalculator);
            reporter.setConfiguration(configuration);

            setBean(rougeNMetric, reporter);
        }

        BiTextPipelineExtractor<List<String>, int[][]> dpMatrixExtractor = new BiTextPipelineExtractor<>();
        BiTextPipelineExtractor<List<String>, boolean[]> someModelMatchExtractor = new BiTextPipelineExtractor<>();
        new BiTextPipeline<>(new DPMatrixBiTextProcessor())
                .cacheIn(CacheMemoryBiTextProcessor::new)
                .extract(dpMatrixExtractor)
                .pipe(new SomeModelMatchContinuation())
                .extract(someModelMatchExtractor);

        List<String> lowerCaseMetrics = configuration.getRequiredMetrics().stream()
                .map(String::trim)
                .map(String::toLowerCase).collect(Collectors.toList());

        if (lowerCaseMetrics.contains(Constants.ROUGEL_LOWER_CASE)) {
            RougeLMultimodelScoreCalculator rougeLMultimodelScoreCalculator = new RougeLMultimodelScoreCalculator();
            rougeLMultimodelScoreCalculator.setDpMatrixProcessor(dpMatrixExtractor.getBiTextProcessor());
            rougeLMultimodelScoreCalculator.setTokensProcessor(tokensExtractor.getTextProcessor());

            PeerMultimodelReporter reporter = new PeerMultimodelReporter();
            reporter.setScoreCalculator(rougeLMultimodelScoreCalculator);
            reporter.setConfiguration(configuration);
            setBean(Constants.ROUGEL_LOWER_CASE, reporter);
        }

        if (lowerCaseMetrics.contains(Constants.ROUGEW_LOWER_CASE)) {
            RougeWMultimodelScoreCalculator rougeWMultimodelScoreCalculator = new RougeWMultimodelScoreCalculator();

            final double weighFactor = 1.2d;
            rougeWMultimodelScoreCalculator.setWeightFunctionSupplier(() -> value -> Math.pow(value, weighFactor));
            rougeWMultimodelScoreCalculator.setInverseWeightFunctionSupplier(() -> value -> Math.pow(value, 1 / weighFactor));
            rougeWMultimodelScoreCalculator.setTokenProcessor(tokensExtractor.getTextProcessor());
            rougeWMultimodelScoreCalculator.setSomeModelMatchProcessor(someModelMatchExtractor.getBiTextProcessor());

            PeerMultimodelReporter reporter = new PeerMultimodelReporter();
            reporter.setScoreCalculator(rougeWMultimodelScoreCalculator);
            reporter.setConfiguration(configuration);

            setBean(Constants.ROUGEW_LOWER_CASE, reporter);
        }

        ContainerConfigData containerConfigData = fetchContainerData(configuration);
        String fileSystemCachePath = containerConfigData.getFileSystemCachePath();

        String coreNLPPipelineAnnotators = "tokenize, ssplit, pos, lemma, truecase, ner, parse";
        Properties coreNLPPipeProperties = new Properties();
        coreNLPPipeProperties.setProperty("annotators", coreNLPPipelineAnnotators);

        Map<String, String> textIdCache = new ConcurrentHashMap<>();

        TextPipelineExtractor<String, String> cachedTextExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Annotation> fsCachedCoreNLPAnnotationExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(cachedTextExtractor)
                .pipe(new CoreNLPTextProcessor(() -> new StanfordCoreNLP(coreNLPPipeProperties)))
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "core-nlp-annotations", textIdCache))
                .extract(fsCachedCoreNLPAnnotationExtractor);

        if (lowerCaseMetrics.contains(Constants.ELENA_READABILITY_LOWER_CASE)) {
            ElenaReadbilityPeersReporter elenaReadbilityPeersReporter = new ElenaReadbilityPeersReporter();

            ElenaReadabilityMetricScoreCalculator readabilityMetricScoreCalculator = new ElenaReadabilityMetricScoreCalculator();
            readabilityMetricScoreCalculator.setDocumentAnnotationProcessor(fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
            readabilityMetricScoreCalculator.setTextReadProcessor(cachedTextExtractor.getTextProcessor());

            elenaReadbilityPeersReporter.setConfiguration(configuration);
            elenaReadbilityPeersReporter.setScoreCalculator(readabilityMetricScoreCalculator);
            setBean(Constants.ELENA_READABILITY_LOWER_CASE, elenaReadbilityPeersReporter);
        }

        if (lowerCaseMetrics.contains(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE)) {
            ElenaReadbilityTopicsReporter elenaReadbilityTopicsReporter = new ElenaReadbilityTopicsReporter();

            ElenaReadabilityMetricScoreCalculator readabilityMetricScoreCalculator = new ElenaReadabilityMetricScoreCalculator();
            readabilityMetricScoreCalculator.setDocumentAnnotationProcessor(fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
            readabilityMetricScoreCalculator.setTextReadProcessor(cachedTextExtractor.getTextProcessor());

            elenaReadbilityTopicsReporter.setConfiguration(configuration);
            elenaReadbilityTopicsReporter.setTopics(containerConfigData.getTopics());
            elenaReadbilityTopicsReporter.setScoreCalculator(readabilityMetricScoreCalculator);

            setBean(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE, elenaReadbilityTopicsReporter);
        }

        List<String> reducers = configuration.getRequiredReducers().stream().map(String::trim).collect(Collectors.toList());
        if (reducers != null && !reducers.isEmpty()) {
            CachedMapRecollector recollector = new CachedMapRecollector();
            recollector.setConfiguration(configuration);

            ReducerStore<Map<String, Object>> reducerStore = new ReducerStore<>(recollector);

            List<Reducer> reducerList = new ArrayList<>();

            for (String reducer : reducers) {
                if (Constants.NORMALIZE_FLESCH_AND_WORD_VARIATION_REDUCER.equals(reducer)) {
                    NormalizeFleschRedabilityAndWordVariationByCorpus normalizeFleschRedabilityReducers =
                            new NormalizeFleschRedabilityAndWordVariationByCorpus();
                    normalizeFleschRedabilityReducers.setStore(reducerStore);
                    reducerList.add(normalizeFleschRedabilityReducers);
                } else if (Constants.SAVE_TO_CSV_REDUCER.equals(reducer)) {
                    FileSystemCSVSaver fileSystemCSVSaver = new FileSystemCSVSaver();
                    fileSystemCSVSaver.setReducerStore(reducerStore);
                    fileSystemCSVSaver.setConfiguration(configuration);
                    reducerList.add(fileSystemCSVSaver);
                }
            }

            CombineReducer combineReducer = new CombineReducer();
            reducerList.forEach(combineReducer::addReducer);

            setBean(Constants.COMBINE_REDUCER, combineReducer);
        }

    }

    protected ContainerConfigData fetchContainerData(Configuration configuration) {
        return ContainerConfigData.fromMap(configuration.getAdditionalContainerConfig());
    }
}