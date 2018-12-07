package il.ac.sce.ir.metric.starter.gui.main.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.NGramTextConfig;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.SimpleTextConfig;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.starter.gui.main.data.MetricWrapper;
import il.ac.sce.ir.metric.starter.gui.main.model.AutoSummENGSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.model.MetricPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.model.ReadabilitySelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.model.RougeSelectionPanelModel;

import java.util.*;
import java.util.stream.Collectors;

public class ModelsToConfigConverter {

    private MetricPanelModel metricPanelModel;

    private RougeSelectionPanelModel rougeSelectionPanelModel;

    private ReadabilitySelectionPanelModel readabilitySelectionPanelModel;

    private AutoSummENGSelectionPanelModel autoSummENGSelectionPanelModel;

    public void setMetricPanelModel(MetricPanelModel metricPanelModel) {
        this.metricPanelModel = metricPanelModel;
    }

    public void setRougeSelectionPanelModel(RougeSelectionPanelModel rougeSelectionPanelModel) {
        this.rougeSelectionPanelModel = rougeSelectionPanelModel;
    }

    public void setReadabilitySelectionPanelModel(ReadabilitySelectionPanelModel readabilitySelectionPanelModel) {
        this.readabilitySelectionPanelModel = readabilitySelectionPanelModel;
    }

    public void setAutoSummENGSelectionPanelModel(AutoSummENGSelectionPanelModel autoSummENGSelectionPanelModel) {
        this.autoSummENGSelectionPanelModel = autoSummENGSelectionPanelModel;
    }

    public Configuration convert() {
        List<String> workingSetMetrics = new ArrayList<>();
        SimpleTextConfig simpleTextConfig = null;
        NGramTextConfig nGramTextConfig = null;
        if (metricPanelModel.isRougeEnabled()) {
            if (rougeSelectionPanelModel.isRougeW()) {
                workingSetMetrics.add(Constants.ROUGEW_LOWER_CASE);
            }
            if (rougeSelectionPanelModel.isRougeL()) {
                workingSetMetrics.add(Constants.ROUGEL_LOWER_CASE);
            }
            List<String> selectedRougeN = rougeSelectionPanelModel.getSelectedNGramMetrics().entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .map(i -> Constants.ROUGEN_LOWER_CASE + i)
                    .collect(Collectors.toList());
            workingSetMetrics.addAll(selectedRougeN);
        }
        if (metricPanelModel.isReadabilityEnabled()) {
            if (readabilitySelectionPanelModel.isTopicsEnabled()) {
                workingSetMetrics.add(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE);
            }
            if (readabilitySelectionPanelModel.isPeersEnabled()) {
                workingSetMetrics.add(Constants.ELENA_READABILITY_LOWER_CASE);
            }
        }
        if (metricPanelModel.isAutoSummENGEnabled()) {
            workingSetMetrics.add(Constants.AUTO_SUMM_ENG_LOWER_CASE);
            if (autoSummENGSelectionPanelModel.isSimpleTextConfigSelected()) {
                simpleTextConfig = autoSummENGSelectionPanelModel.getSimpleTextConfig();
            }
            if (autoSummENGSelectionPanelModel.isCharNGramSelected()) {
                nGramTextConfig = autoSummENGSelectionPanelModel.getnGramTextConfig();
            }
        }
        String workingSetDirectory = metricPanelModel.getChosenMetricsDirectory();
        workingSetDirectory = correctWindowsDriveLetters(workingSetDirectory);
        String containerClass = "il.ac.sce.ir.metric.starter.command_line.main.container.DefaultContainerImpl";
        String mainAlgoClass = "il.ac.sce.ir.metric.starter.command_line.main.container.algo.MainAlgoDefaultImpl";
        String resultDirectory = "result";
        String cacheDirectory = "cache";
        List<String> postMetricProcesing = Arrays.asList(
                Constants.NORMALIZE_FLESCH_AND_WORD_VARIATION_REDUCER, Constants.SAVE_TO_CSV_REDUCER);
        Configuration.Mirror configurationMirror = Configuration.as()
                .workingSetDirectory(workingSetDirectory)
                .resultDirectory(resultDirectory)
                .containerClass(containerClass)
                .mainAlgoClass(mainAlgoClass);
        workingSetMetrics.forEach(configurationMirror::addRequiredMetric);
        postMetricProcesing.forEach(configurationMirror::addRequiredReducer);
        configurationMirror.autoSummENGWord(simpleTextConfig == null ? null : simpleTextConfig.toMap());
        configurationMirror.autoSummENGChar(nGramTextConfig == null ? null : nGramTextConfig.toMap());
        Map<String, Object> additionalContainerConfig = new HashMap<>();
        additionalContainerConfig.put(Constants.FILE_SYSTEM_CACHE_PATH, cacheDirectory);
        configurationMirror.additionalContainerConfig(additionalContainerConfig);

        return configurationMirror.build();
    }

    private String correctWindowsDriveLetters(String workingSetDirectory) {
        if (workingSetDirectory.charAt(1) == ':') {
            char c = workingSetDirectory.charAt(0);
            c = Character.toLowerCase(c);
            return c + workingSetDirectory.substring(1);
        }
        return workingSetDirectory;
    }
}
