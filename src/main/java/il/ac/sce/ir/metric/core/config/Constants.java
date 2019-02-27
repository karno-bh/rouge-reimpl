package il.ac.sce.ir.metric.core.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface Constants {

    // SEP = Summary Evaluation Platform
    String DEFAULT_CONFIG_FILE = "sep-config.json";
    String CONFIG_FILE_KEY = "--sepConfig";
    String HELP_FILE_KEY = "--help";

    String WORKING_SET_DIRECTORY = "workingSetDirectory";
    String WORKING_SET_METRICS = "workingSetMetrics";
    String WORKING_SET_FILTERS = "workingSetFilters";
    String CONTAINER_CLASS = "containerClass";
    String MAIN_ALGO_CLASS = "mainAlgoClass";
    String ADDITIONAL_CONTAINER_CONFIG = "additionalContainerConfig";
    String FILE_SYSTEM_CACHE_PATH = "fileSystemCachePath";
    String ORIGINAL_TOPICS = "topics";
    String RESULT_DIRECTORY = "resultDirectory";
    String POST_METRICS_PROCESSING = "postMetricsProcessing";
    String GEORGE_GRAPH_METRIC_CONFIG = "georgeGraphMetricConfig";

    String LOWER_CASE_FILTER = "lowerCaseFilter";
    String PUNCTUATION_FILTER = "punctuationFilter";
    String STOP_WORDS_REMOVAL_FILTER = "stopWordsRemovalFilter";
    String PORTER_STEMMER_FILTER = "porterStemmerFilter";

    String ROUGE_LOWER_CASE = "rouge";
    String ROUGEN_LOWER_CASE = "rougen";
    String ROUGEL_LOWER_CASE = "rougel";
    String ROUGEW_LOWER_CASE = "rougew";
    String ROUGES_LOWER_CASE = "rouges";
    String ROUGESU_LOWER_CASE = "rougesu";
    String READABILITY_LOWER_CASE = "readability";
    String TOPICS_READABILITY_LOWER_CASE = "topics_readability";
    String ELENA_READABILITY_LOWER_CASE = "elena_readability";
    String ELENA_TOPICS_READABILITY_LOWER_CASE = "elena_topics_readability";
    String READABILITY_PRE_CACHE = "readability_pre_cache";
    String GEORGE_GRAPH_METRIC_LOWER_CASE = "george_graph_metric";
    String AUTO_SUMM_ENG_LOWER_CASE = "auto_summ_eng";
    String[] METRICS_FOR_SYSTEM_TO_BE_REDUCED = {
            /*ROUGEN_LOWER_CASE,
            ROUGES_LOWER_CASE,
            ROUGEL_LOWER_CASE,
            ROUGEW_LOWER_CASE,*/
            ELENA_READABILITY_LOWER_CASE,
            ELENA_TOPICS_READABILITY_LOWER_CASE,
    };
    String[] METRICS_WITH_CATEGORY_SYSTEM_METRIC_FORMAT = {
            ROUGEN_LOWER_CASE,
            ROUGES_LOWER_CASE,
            ROUGEW_LOWER_CASE,
            ROUGEL_LOWER_CASE,
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
    String VIRTUAL_TOPIC_SYSTEM = "__topic__";
    String MODEL = "model";
    String CSV_EXTENSION = ".csv";
    String SVG_EXTENSION = ".svg";

    String FLESCH_READING_EASE = "fleschReadingEase";
    String FLESCH_READING_EASE_NORMALIZED = "fleschReadingEaseNormalized";
    String PROPER_NOUN_RATIO = "properNounRatio";
    String UNIQUE_PROPER_NOUN_RATIO = "uniqueProperNounsRatio";
    String WORD_VARIATION_INDEX = "wordVariationIndex";
    String WORD_VARIATION_INDEX_NORMALIZED = "wordVariationIndexNormalized";
    String AVERAGE_WORD_LENGTH = "averageWordLength";
    String AVERAGE_WORD_LENGTH_NORMALIZED = "averageWordLengthNormalized";
    String AVERAGE_SENTENCE_LENGTH = "averageSentenceLength";
    String AVERAGE_SENTENCE_LENGTH_NORMALIZED = "averageSentenceLengthNormalized";
    String NOUN_RATIO = "nounRatio";
    // String NOUN_RATIO_NORMALIZED = "nounRatioNormalized";
    String PRONOUN_RATIO = "pronounRatio";
    // String PRONOUN_RATIO_NORMALIZED = "pronounRatioNormalized";

    String[] ELENA_READABILITY_SUB_METRICS = {
            FLESCH_READING_EASE,
            FLESCH_READING_EASE_NORMALIZED,
            PROPER_NOUN_RATIO,
            UNIQUE_PROPER_NOUN_RATIO,
            WORD_VARIATION_INDEX,
            WORD_VARIATION_INDEX_NORMALIZED,
            AVERAGE_WORD_LENGTH,
            AVERAGE_WORD_LENGTH_NORMALIZED,
            AVERAGE_SENTENCE_LENGTH,
            AVERAGE_SENTENCE_LENGTH_NORMALIZED,
            NOUN_RATIO,
            PRONOUN_RATIO
    };

    String PRECISION = "precision";
    String RECALL = "recall";
    String ALPHA = "alpha";
    String F1_MEASURE = "f1Measure";
    String[] ROUGE_SUB_METRICS = {
            PRECISION,
            RECALL,
            F1_MEASURE
    };

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

    String[] AUTO_SUMM_ENG_WORD_GRAPH_SUB_METRICS = {
            AUTO_SUMM_ENG_GRAPH_COOCCURENCE,
            AUTO_SUMM_ENG_GRAPH_VALUE,
            AUTO_SUMM_ENG_GRAPH_SIZE,
    };

    String[] AUTO_SUMM_ENG_WORD_HISTOGRAM_SUB_METRICS = {
            AUTO_SUMM_ENG_HISTO_CONTAINMENT_SIMILARITY,
            AUTO_SUMM_ENG_HISTO_VALUE,
            AUTO_SUMM_ENG_HISTO_SIZE,
    };

    String[] AUTO_SUMM_ENG_WORD_SUB_METRICS = {
            AUTO_SUMM_ENG_GRAPH_COOCCURENCE,
            AUTO_SUMM_ENG_GRAPH_VALUE,
            AUTO_SUMM_ENG_GRAPH_SIZE,
            AUTO_SUMM_ENG_HISTO_CONTAINMENT_SIMILARITY,
            AUTO_SUMM_ENG_HISTO_VALUE,
            AUTO_SUMM_ENG_HISTO_SIZE,
            AUTO_SUMM_ENG_HISTO_OVERALL_SIMIL
    };

    String AUTO_SUMM_ENG_CHAR_GRAPH_COOCCURENCE = "CharGraphCooccurence";
    String AUTO_SUMM_ENG_CHAR_GRAPH_VALUE = "CharGraphValue";
    String AUTO_SUMM_ENG_CHAR_GRAPH_SIZE = "CharGraphSize";
    String AUTO_SUMM_ENG_N_HISTO_CONTAINMENT_SIMILARITY = "NHistoContainmentSimilarity";
    String AUTO_SUMM_ENG_N_HISTO_VALUE = "NHistoValue";
    String AUTO_SUMM_ENG_N_HISTO_SIZE = "NHistoSize";
    String AUTO_SUMM_ENG_N_HISTO_OVERALL_SIMIL = "NOverallSimil";

    String[] AUTO_SUMM_ENG_CHAR_GRAPH_SUB_METRICS = {
            AUTO_SUMM_ENG_CHAR_GRAPH_COOCCURENCE,
            AUTO_SUMM_ENG_CHAR_GRAPH_VALUE,
            AUTO_SUMM_ENG_CHAR_GRAPH_SIZE,
    };

    String[] AUTO_SUMM_ENG_CHAR_HISTOGRAM_SUB_METRICS = {
            AUTO_SUMM_ENG_N_HISTO_CONTAINMENT_SIMILARITY,
            AUTO_SUMM_ENG_N_HISTO_VALUE,
            AUTO_SUMM_ENG_N_HISTO_SIZE,
    };

    String[] AUTO_SUMM_ENG_CHAR_SUB_METRICS = {
            AUTO_SUMM_ENG_CHAR_GRAPH_COOCCURENCE,
            AUTO_SUMM_ENG_CHAR_GRAPH_VALUE,
            AUTO_SUMM_ENG_CHAR_GRAPH_SIZE,
            AUTO_SUMM_ENG_N_HISTO_CONTAINMENT_SIMILARITY,
            AUTO_SUMM_ENG_N_HISTO_VALUE,
            AUTO_SUMM_ENG_N_HISTO_SIZE,
            AUTO_SUMM_ENG_N_HISTO_OVERALL_SIMIL,
    };

    String AUTO_SUMM_ENG_WORDS = "autoSummENGWords";
    String AUTO_SUMM_ENG_CHARS = "autoSummENGChars";

    String MIN = "min";
    String MAX = "max";
    String DIST = "dist";

    String RESULT_DIRECTORY_DEFAULT = "result";
    String CACHE_DIRECTORY_DEFAULT = "cache";
    String TEMP_DIRECTORY_DEFAULT = "temp";
    String ALL_RESULT_JSON_FILE_NAME = "all_results.json";
    String AVERAGES_BY_SYSTEMS_FILE_NAME = "averages_by_systems.json";

    Map<String, String> TOPICS_METRICS_MAPPING = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put(ELENA_TOPICS_READABILITY_LOWER_CASE, ELENA_READABILITY_LOWER_CASE);
    }});

    Map<String, String> DISPLAY_FILTER_ELENA_METRIC = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put(ELENA_READABILITY_LOWER_CASE, READABILITY_LOWER_CASE);
        put(ELENA_TOPICS_READABILITY_LOWER_CASE, TOPICS_READABILITY_LOWER_CASE);
    }});

    String SCRIPT_R_HSD_TEST = "hsd_test.r";
    String SCRIPT_BAT_HSD_TEST = "run_hsd_test.bat";
    String DEFAULT_CSV_FILE_RPC = "temp.csv";
    String DEFAULT_HSD_TEST_RESULT = "hsd_result.txt";

    String AVERAGE_VIRTUAL_ROW = "__average__";
}
