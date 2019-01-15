package il.ac.sce.ir.metric.core.config;

public interface Constants {

    // SEP = Summary Evaluation Platform
    String DEFAULT_CONFIG_FILE = "sep-config.json";
    String CONFIG_FILE_KEY = "--sepConfig";
    String HELP_FILE_KEY = "--help";

    String WORKING_SET_DIRECTORY = "workingSetDirectory";
    String WORKING_SET_METRICS = "workingSetMetrics";
    String CONTAINER_CLASS = "containerClass";
    String MAIN_ALGO_CLASS = "mainAlgoClass";
    String ADDITIONAL_CONTAINER_CONFIG = "additionalContainerConfig";
    String FILE_SYSTEM_CACHE_PATH = "fileSystemCachePath";
    String ORIGINAL_TOPICS = "topics";
    String RESULT_DIRECTORY = "resultDirectory";
    String POST_METRICS_PROCESSING = "postMetricsProcessing";
    String GEORGE_GRAPH_METRIC_CONFIG = "georgeGraphMetricConfig";

    String ROUGEN_LOWER_CASE = "rougen";
    String ROUGEL_LOWER_CASE = "rougel";
    String ROUGEW_LOWER_CASE = "rougew";
    String ROUGES_LOWER_CASE = "rouges";
    String ROUGESU_LOWER_CASE = "rougesu";
    String READABILITY_LOWER_CASE = "readability";
    String ELENA_READABILITY_LOWER_CASE = "elena_readability";
    String ELENA_TOPICS_READABILITY_LOWER_CASE = "elena_topics_readability";
    String READABILITY_PRE_CACHE = "readability_pre_cache";
    String GEORGE_GRAPH_METRIC_LOWER_CASE = "george_graph_metric";
    String AUTO_SUMM_ENG_LOWER_CASE = "auto_summ_eng";
    String[] METRICS_FOR_SYSTEM = {
            /*ROUGEN_LOWER_CASE,
            ROUGES_LOWER_CASE,
            ROUGEL_LOWER_CASE,
            ROUGEW_LOWER_CASE,*/
            ELENA_READABILITY_LOWER_CASE,
            ELENA_TOPICS_READABILITY_LOWER_CASE,
    };

    String DESCRIPTION_FILE = ".description";

    String UTF8 = "UTF-8";
    String PEERS_DIRECTORY = "peers";
    String MODELS_DIRECTORY = "models";
    String TOPICS = "topics";
    String RESULT_FILE_ENITITIES_SEPARATOR = "_";
    String CSV_REPORT_SEPARATOR = ",";
    String CATEGORY = "category";
    String SYSTEM = "system";
    String PEER = "peer";
    String TOPIC = "topic";
    String MODEL = "model";
    String CSV_EXTENSION = ".csv";

    String FLESCH_READING_EASE = "fleschReadingEase";
    String FLESCH_READING_EASE_NORMALIZED = "fleschReadingEaseNormalized";
    String PROPER_NOUN_RATION = "properNounRatio";
    String UNIQUE_PROPER_NOUN_RATIO = "uniqueProperNounsRatio";
    String WORD_VARIATION_INDEX = "wordVariationIndex";
    String WORD_VARIATION_INDEX_NORMALIZED = "wordVariationIndexNormalized";


    String PRECISION = "precision";
    String RECALL = "recall";
    String ALPHA = "alpha";
    String F1_MEASURE = "f1Measure";

    String REDUCERS_DUMP_DIRECTORY = "reduced";

    String NORMALIZE_FLESCH_AND_WORD_VARIATION_REDUCER = "normalizeFleschAndWordVariation";
    String SAVE_TO_CSV_REDUCER = "saveToCSV";

    String COMBINE_REDUCER = "combineReducer";

    String GEORGE_METHOD_N_GRAMS = "ngrams";
    String GEORGE_METHOD_WORD_GRAPHS = "wordGraphs";
    String GEORGE_METHOD_PLACEHOLDER = "placeholder";
    String GEORGE_METHOD_PLACEHOLDER_SS = "placeholder_ss";
    String GEORGE_METHOD_RANDOM = "random";
    String GEORGE_METHOD_COSINE = "cosine";
    String GEORGE_METHOD_PLACE_HOLDER_EXTRAWEIGHT = "placeholder_extra_weight";

    String MAIN_TRHEAD_POOL = "mainThreadPool";
    String ARBITER = "arbiter";

    String AUTO_SUMM_ENG_GRAPH_COOCCURENCE = "GraphCooccurence";
    String AUTO_SUMM_ENG_GRAPH_VALUE = "GraphValue";
    String AUTO_SUMM_ENG_GRAPH_SIZE = "GraphSize";
    String AUTO_SUMM_ENG_HISTO_CONTAINMENT_SIMILARITY = "HistoContainmentSimilarity";
    String AUTO_SUMM_ENG_HISTO_VALUE = "HistoValue";
    String AUTO_SUMM_ENG_HISTO_SIZE = "HistoSize";
    String AUTO_SUMM_ENG_HISTO_OVERALL_SIMIL = "OverallSimil";

    String AUTO_SUMM_ENG_CHAR_GRAPH_COOCCURENCE = "CharGraphValue";
    String AUTO_SUMM_ENG_CHAR_GRAPH_VALUE = "CharGraphValue";
    String AUTO_SUMM_ENG_CHAR_GRAPH_SIZE = "CharGraphSize";
    String AUTO_SUMM_ENG_N_HISTO_CONTAINMENT_SIMILARITY = "NHistoContainmentSimilarity";
    String AUTO_SUMM_ENG_N_HISTO_VALUE = "NHistoValue";
    String AUTO_SUMM_ENG_N_HISTO_SIZE = "NHistoSize";
    String AUTO_SUMM_ENG_N_HISTO_OVERALL_SIMIL = "NOverallSimil";

    String AUTO_SUMM_ENG_WORDS = "autoSummENGWords";
    String AUTO_SUMM_ENG_CHARS = "autoSummENGChars";

    String MIN = "min";
    String MAX = "max";
    String DIST = "dist";
}
