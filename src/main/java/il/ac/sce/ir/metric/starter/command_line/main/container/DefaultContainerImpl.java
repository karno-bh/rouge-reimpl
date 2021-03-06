package il.ac.sce.ir.metric.starter.command_line.main.container;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.NGramTextConfig;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.SimpleTextConfig;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor.JInsectNGramSymWinDocumentTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor.JInsectSimpleTextDocumentTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.reporter.AutoSummENGReporter;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator.AutoSummENGNGramScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator.AutoSummENGScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator.AutoSummENGSimpleTextScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.common.util.ParallelPreCache;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.*;
import il.ac.sce.ir.metric.core.processor.token_filter.LowerCaseFilterProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.PorterStemmerFilterProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.PunctuationFilterProcessor;
import il.ac.sce.ir.metric.core.processor.token_filter.StopwordsRemovalFilterProcessor;
import il.ac.sce.ir.metric.core.sync.Arbiter;
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
import il.ac.sce.ir.metric.concrete_metric.elena.reporter.ElenaReadabilityPeersReporter;
import il.ac.sce.ir.metric.concrete_metric.elena.reporter.ElenaReadbilityTopicsReporter;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.common.nlp.processor.CoreNLPTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.PeerMultimodelReporter;
import il.ac.sce.ir.metric.concrete_metric.rouge.score_calculator.RougeLMultimodelScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.rouge.score_calculator.RougeNMultimodelScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.rouge.score_calculator.RougeWMultimodelScoreCalculator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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


        Arbiter arbiter = new Arbiter();
        setBean(Constants.ARBITER, arbiter);

        // some empiric number...
        // TODO number should come from configuration
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        setBean(Constants.MAIN_TRHEAD_POOL, executorService);

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

        List<String> filters = configuration.getRequiredFilters();
        filters = filters == null ? new ArrayList<>() : filters;

        TextPipeline<String, List<String>> initialPipeline = new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .pipeIf(filters.contains(Constants.LOWER_CASE_FILTER), new LowerCaseFilterProcessor())
                .pipeIf(filters.contains(Constants.PUNCTUATION_FILTER), new PunctuationFilterProcessor())
                .pipeIf(filters.contains(Constants.STOP_WORDS_REMOVAL_FILTER), new StopwordsRemovalFilterProcessor())
                .pipeIf(filters.contains(Constants.PORTER_STEMMER_FILTER), new PorterStemmerFilterProcessor())
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
            reporter.setExecutorService(executorService);
            arbiter.register(rougeNMetric);
            reporter.setArbiter(arbiter);
            reporter.setMetricName(rougeNMetric);

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

        List<String> rougeSMetrics = lowerCaseMetrics.stream()
                .filter(metric -> metric.contains(Constants.ROUGES_LOWER_CASE))
                .collect(Collectors.toList());

        for (String rougeSMetric : rougeSMetrics) {
            // String rougeSMetric = rougeSMetrics.get(0);
            TextPipelineExtractor<String, Map<String, Integer>> rougeSGramExtractor = new TextPipelineExtractor<>();
            boolean useUnigrams = rougeSMetric.contains(Constants.ROUGESU_LOWER_CASE);
            String rougeSIndexStr = rougeSMetric.substring(
                    useUnigrams ? Constants.ROUGESU_LOWER_CASE.length() : Constants.ROUGES_LOWER_CASE.length()
            );
            int skipDistance = 0;
            if (rougeSIndexStr.length() > 0) {
                skipDistance = Integer.parseInt(rougeSIndexStr);
            }
            initialPipeline.pipe(new SGramTextProcessor(useUnigrams, skipDistance))
                    .cacheIn(CacheMemoryTextProcessor::new)
                    .extract(rougeSGramExtractor);

            NGramHits nGramHits = new NGramHits(rougeSGramExtractor.getTextProcessor());
            RougeNMultimodelScoreCalculator rougeNMultimodelReporter = new RougeNMultimodelScoreCalculator();

            rougeNMultimodelReporter.setnGramProcessor(rougeSGramExtractor.getTextProcessor());
            rougeNMultimodelReporter.setnGramHits(nGramHits);

            PeerMultimodelReporter reporter = new PeerMultimodelReporter();
            reporter.setScoreCalculator(rougeNMultimodelReporter);
            reporter.setConfiguration(configuration);
            reporter.setExecutorService(executorService);
            arbiter.register(rougeSMetric);
            reporter.setArbiter(arbiter);
            reporter.setMetricName(rougeSMetric);

            setBean(rougeSMetric, reporter);
        }

        if (lowerCaseMetrics.contains(Constants.ROUGEL_LOWER_CASE)) {
            RougeLMultimodelScoreCalculator rougeLMultimodelScoreCalculator = new RougeLMultimodelScoreCalculator();
            rougeLMultimodelScoreCalculator.setDpMatrixProcessor(dpMatrixExtractor.getBiTextProcessor());
            rougeLMultimodelScoreCalculator.setTokensProcessor(tokensExtractor.getTextProcessor());

            PeerMultimodelReporter reporter = new PeerMultimodelReporter();
            reporter.setScoreCalculator(rougeLMultimodelScoreCalculator);
            reporter.setConfiguration(configuration);
            reporter.setExecutorService(executorService);
            arbiter.register(Constants.ROUGEL_LOWER_CASE);
            reporter.setArbiter(arbiter);
            reporter.setMetricName(Constants.ROUGEL_LOWER_CASE);

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
            reporter.setExecutorService(executorService);
            arbiter.register(Constants.ROUGEW_LOWER_CASE);
            reporter.setArbiter(arbiter);
            reporter.setMetricName(Constants.ROUGEW_LOWER_CASE);

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
        TextPipelineExtractor<String, Annotation> coreNLPAnnotationExtractor = new TextPipelineExtractor<>();
        // FileSystemCacheTextProcessor fileSystemCacheTextProcessor = new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "core-nlp-annotations", textIdCache);
        new TextPipeline<>(new FileToStringProcessor())
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(cachedTextExtractor)
                .pipe(new CoreNLPTextProcessor(() -> new StanfordCoreNLP(coreNLPPipeProperties)))
                .extract(coreNLPAnnotationExtractor)
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "core-nlp-annotations", textIdCache))
                .extract(fsCachedCoreNLPAnnotationExtractor);
        ParallelPreCache parallelPreCache = new ParallelPreCache();
        parallelPreCache.setTextProcessor(coreNLPAnnotationExtractor.getTextProcessor());
        parallelPreCache.setFileSystemCacheTextProcessor((FileSystemCacheTextProcessor<String, Annotation>) fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
        setBean(Constants.READABILITY_PRE_CACHE, parallelPreCache);
        if (lowerCaseMetrics.contains(Constants.ELENA_READABILITY_LOWER_CASE)) {
            parallelPreCache.setPeersEnabled(true);
            ElenaReadabilityPeersReporter elenaReadabilityPeersReporter = new ElenaReadabilityPeersReporter();

            ElenaReadabilityMetricScoreCalculator readabilityMetricScoreCalculator = new ElenaReadabilityMetricScoreCalculator();
            readabilityMetricScoreCalculator.setDocumentAnnotationProcessor(fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
            readabilityMetricScoreCalculator.setTextReadProcessor(cachedTextExtractor.getTextProcessor());

            elenaReadabilityPeersReporter.setConfiguration(configuration);
            elenaReadabilityPeersReporter.setScoreCalculator(readabilityMetricScoreCalculator);
            elenaReadabilityPeersReporter.setExecutorService(executorService);
            arbiter.register(Constants.ELENA_READABILITY_LOWER_CASE);
            elenaReadabilityPeersReporter.setArbiter(arbiter);
            elenaReadabilityPeersReporter.setMetricName(Constants.ELENA_READABILITY_LOWER_CASE);

            setBean(Constants.ELENA_READABILITY_LOWER_CASE, elenaReadabilityPeersReporter);
        }

        if (lowerCaseMetrics.contains(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE)) {
            parallelPreCache.setTopicsEnabled(true);
            ElenaReadbilityTopicsReporter elenaReadbilityTopicsReporter = new ElenaReadbilityTopicsReporter();

            ElenaReadabilityMetricScoreCalculator readabilityMetricScoreCalculator = new ElenaReadabilityMetricScoreCalculator();
            readabilityMetricScoreCalculator.setDocumentAnnotationProcessor(fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
            readabilityMetricScoreCalculator.setTextReadProcessor(cachedTextExtractor.getTextProcessor());

            elenaReadbilityTopicsReporter.setConfiguration(configuration);
            elenaReadbilityTopicsReporter.setTopics(containerConfigData.getTopics());
            elenaReadbilityTopicsReporter.setScoreCalculator(readabilityMetricScoreCalculator);

            arbiter.register(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE);
            elenaReadbilityTopicsReporter.setArbiter(arbiter);
            elenaReadbilityTopicsReporter.setExecutorService(executorService);

            setBean(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE, elenaReadbilityTopicsReporter);
        }


        if (lowerCaseMetrics.contains(Constants.AUTO_SUMM_ENG_LOWER_CASE)) {

            TextPipelineExtractor<DocumentDesc, SimpleTextDocument> simpleTextDocumentTextPipelineExtractor = new TextPipelineExtractor<>();
            new TextPipeline<>(new JInsectSimpleTextDocumentTextProcessor())
                    .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "jinsect-simple-text-document", textIdCache))
                    // .cacheIn(CacheMemoryTextProcessor::new)
                    .extract(simpleTextDocumentTextPipelineExtractor);
            TextProcessor<DocumentDesc, SimpleTextDocument> simpleTextDocumentTextProcessor = simpleTextDocumentTextPipelineExtractor.getTextProcessor();

            TextPipelineExtractor<DocumentDesc, NGramSymWinDocument> nGramSymWinDocumentTextPipelineExtractor = new TextPipelineExtractor<>();
            new TextPipeline<>(new JInsectNGramSymWinDocumentTextProcessor())
                    .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "jinsect-ngram-sym-win-document", textIdCache))
                    // .cacheIn(CacheMemoryTextProcessor::new)
                    .extract(nGramSymWinDocumentTextPipelineExtractor);
            TextProcessor<DocumentDesc, NGramSymWinDocument> nGramSymWinDocumentTextProcessor = nGramSymWinDocumentTextPipelineExtractor.getTextProcessor();

            AutoSummENGScoreCalculator simpleTextScoreCalculator = new AutoSummENGSimpleTextScoreCalculator(simpleTextDocumentTextProcessor);
            AutoSummENGScoreCalculator nGramScoreCalculator = new AutoSummENGNGramScoreCalculator(nGramSymWinDocumentTextProcessor);

            Map<String, Object> autoSummENGWord = configuration.getAutoSummENGWord();
            SimpleTextConfig simpleTextConfig = autoSummENGWord == null ? null : SimpleTextConfig.fromMap(autoSummENGWord);

            Map<String, Object> autoSummENGChar = configuration.getAutoSummENGChar();
            NGramTextConfig nGramTextConfig = autoSummENGChar == null ? null : NGramTextConfig.fromMap(autoSummENGChar);

            AutoSummENGReporter reporter = new AutoSummENGReporter();
            reporter.setSimpleTextConfig(simpleTextConfig);
            reporter.setnGramTextConfig(nGramTextConfig);

            reporter.setRequiredFilters(configuration.getRequiredFilters());
            reporter.setFilterTextProcessor(tokensExtractor.getTextProcessor());

            reporter.setSimpleTextDocumentTextProcessor(simpleTextScoreCalculator);
            reporter.setnGramSymWinDocumentTextProcessor(nGramScoreCalculator);

            reporter.setConfiguration(configuration);
            reporter.setExecutorService(executorService);

            arbiter.register(Constants.AUTO_SUMM_ENG_LOWER_CASE);
            reporter.setArbiter(arbiter);

            setBean(Constants.AUTO_SUMM_ENG_LOWER_CASE, reporter);
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
