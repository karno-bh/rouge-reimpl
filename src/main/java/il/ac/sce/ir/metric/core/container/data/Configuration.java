package il.ac.sce.ir.metric.core.container.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Configuration {

    private static final Utils utils = new Utils();

    private final String workingSetDirectory;
    private final List<String> requiredMetrics;
    private final String containerClass;
    private final String mainAlgoClass;
    private final Map<String, Object> additionalContainerConfig;
    private final String resultDirectory;
    private final List<String> requiredReducers;
    private final Map<String, Object> autoSummENGWord;
    private final Map<String, Object> autoSummENGChar;

    private Configuration(Mirror mirror) {
        this.workingSetDirectory = mirror.workingSetDirectory;
        this.requiredMetrics = mirror.requiredMetrics;
        this.containerClass = mirror.containerClass;
        this.mainAlgoClass = mirror.mainAlgoClass;
        this.additionalContainerConfig = mirror.additionalContainerConfig;
        this.resultDirectory = mirror.resultDirectory;
        this.requiredReducers = mirror.requiredReducers;
        this.autoSummENGWord = mirror.autoSummENGWord;
        this.autoSummENGChar = mirror.autoSummENGChar;
    }

    public String getWorkingSetDirectory() {
        return workingSetDirectory;
    }

    public List<String> getRequiredMetrics() {
        return requiredMetrics;
    }

    public String getContainerClass() {
        return containerClass;
    }

    public String getMainAlgoClass() {
        return mainAlgoClass;
    }

    public Map<String, Object> getAdditionalContainerConfig() {
        return additionalContainerConfig;
    }

    public String getResultDirectory() {
        return resultDirectory;
    }

    public List<String> getRequiredReducers() {
        return requiredReducers;
    }

    public Map<String, Object> getAutoSummENGWord() {
        return autoSummENGWord;
    }

    public Map<String, Object> getAutoSummENGChar() {
        return autoSummENGChar;
    }

    public static Configuration fromFileName(String configFileName) {
        Mirror mirror = loadConfigFile(configFileName);
        return new Configuration(mirror);
    }

    public static Mirror as() {
        return new Mirror();
    }

    private static Mirror loadConfigFile(String configFileName) {
        File configFile = new File(configFileName);
        if (!configFile.isFile()) {
            utils.printErrorMessageAndExitProcess("Cannot load config file: \"" + configFileName + "\"");
        }
        Map<String, Object> jsonData;
        try {
            jsonData = new ObjectMapper().readValue(configFile, LinkedHashMap.class);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while loading SEP config file", ioe);
        }

        String workingSetDirectory = utils.requireJSONTypeAndCast(jsonData.get(Constants.WORKING_SET_DIRECTORY), Constants.WORKING_SET_DIRECTORY, String.class);
        File workingSetDirectoryFile = new File(workingSetDirectory);
        if (!workingSetDirectoryFile.isDirectory()) {
            throw new RuntimeException("Working set directory could not be found");
        }
        Mirror mirror = new Mirror();
        mirror.workingSetDirectory = workingSetDirectory;

        List requiredMetricsFromJson = utils.requireJSONTypeAndCast(jsonData.get(Constants.WORKING_SET_METRICS), Constants.WORKING_SET_METRICS, List.class);
        List<String> requiredMetrics = new ArrayList<>(requiredMetricsFromJson.size());
        for (Object requiredMetricObj : requiredMetricsFromJson) {
            String requiredMetric = utils.requireJSONTypeAndCast(requiredMetricObj, "working set array element", String.class);
            requiredMetrics.add(requiredMetric);
        }
        mirror.requiredMetrics = Collections.unmodifiableList(requiredMetrics);
        mirror.containerClass = utils.requireJSONTypeAndCast(jsonData.get(Constants.CONTAINER_CLASS), Constants.CONTAINER_CLASS, String.class);
        mirror.mainAlgoClass = utils.requireJSONTypeAndCast(jsonData.get(Constants.MAIN_ALGO_CLASS), Constants.MAIN_ALGO_CLASS, String.class);
        mirror.additionalContainerConfig = utils.requireJSONTypeAndCast(jsonData.get(Constants.ADDITIONAL_CONTAINER_CONFIG), Constants.ADDITIONAL_CONTAINER_CONFIG, Map.class);
        mirror.autoSummENGWord = utils.requireJSONTypeAndCast(jsonData.get(Constants.AUTO_SUMM_ENG_WORDS), Constants.AUTO_SUMM_ENG_WORDS, Map.class);
        mirror.autoSummENGChar = utils.requireJSONTypeAndCast(jsonData.get(Constants.AUTO_SUMM_ENG_CHARS), Constants.AUTO_SUMM_ENG_CHARS, Map.class);
        mirror.resultDirectory = utils.requireJSONTypeAndCast(jsonData.get(Constants.RESULT_DIRECTORY), Constants.RESULT_DIRECTORY, String.class);
        List requiredReducersFromJSON = utils.requireJSONTypeAndCast(jsonData.get(Constants.POST_METRICS_PROCESSING), Constants.POST_METRICS_PROCESSING, List.class);
        List<String> requiredReducers = new ArrayList<>();
        for (Object requiredReducerObj : requiredReducersFromJSON) {
            String requiredReducer = utils.requireJSONTypeAndCast(requiredReducerObj, "post metrics array element", String.class);
            requiredReducers.add(requiredReducer);
        }
        mirror.requiredReducers = requiredReducers;
        mirror.georgeGraphMetricConfig = utils.requireJSONTypeAndCast(jsonData.get(Constants.GEORGE_GRAPH_METRIC_CONFIG), Constants.GEORGE_GRAPH_METRIC_CONFIG, Map.class);

        return mirror;
    }

    public static class Mirror {

        private String workingSetDirectory;
        private List<String> requiredMetrics = new ArrayList<>();
        private String containerClass;
        private String mainAlgoClass;
        private Map<String, Object> additionalContainerConfig;
        private String resultDirectory;
        private List<String> requiredReducers = new ArrayList<>();
        private Map<String, Object> georgeGraphMetricConfig;
        private Map<String, Object> autoSummENGWord;
        private Map<String, Object> autoSummENGChar;

        public Mirror workingSetDirectory(String workingSetDirectory) {
            this.workingSetDirectory = workingSetDirectory;
            return this;
        }

        public Mirror addRequiredMetric(String metric) {
            requiredMetrics.add(metric);
            return this;
        }

        public Mirror containerClass(String containerClass) {
            this.containerClass = containerClass;
            return this;
        }

        public Mirror mainAlgoClass(String mainAlgoClass) {
            this.mainAlgoClass = mainAlgoClass;
            return this;
        }

        public Mirror additionalContainerConfig(Map<String, Object> additionalContainerConfig) {
            this.additionalContainerConfig = additionalContainerConfig;
            return this;
        }

        public Mirror resultDirectory(String resultDirectory) {
            this.resultDirectory = resultDirectory;
            return this;
        }

        public Mirror addRequiredReducer(String requiredReducer) {
            this.requiredReducers.add(requiredReducer);
            return this;
        }

        public Mirror autoSummENGWord(Map<String, Integer> wordConfig) {
            this.autoSummENGWord = (Map)wordConfig;
            return this;
        }

        public Mirror autoSummENGChar(Map<String, Integer> charConfig) {
            this.autoSummENGChar = (Map)charConfig;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }

    }
}
