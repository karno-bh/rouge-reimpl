package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.file_system.KnownMetricFormatsHierarchicalOrganizer;
import il.ac.sce.ir.metric.core.utils.results.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FileChoosePanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.AnalyzePanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import java.io.File;
import java.util.Map;

public class AnalyzePanelModel implements AppModel {

    private final PubSub pubSub;

    private String chosenResultDirectory;

    private ResultsMetricHierarchyAnalyzer resultsMetricHierarchyAnalyzer;

    public String getChosenResultDirectory() {
        return chosenResultDirectory;
    }

    public void setChosenResultDirectory(String chosenResultDirectory) {
        this.chosenResultDirectory = chosenResultDirectory;
    }

    public ResultsMetricHierarchyAnalyzer getResultsMetricHierarchyAnalyzer() {
        return resultsMetricHierarchyAnalyzer;
    }

    public void setResultsMetricHierarchyAnalyzer(ResultsMetricHierarchyAnalyzer resultsMetricHierarchyAnalyzer) {
        this.resultsMetricHierarchyAnalyzer = resultsMetricHierarchyAnalyzer;
    }

    public AnalyzePanelModel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(FileChoosePanelEvent.class, this::onResultDirectoryChanged);
    }

    private void onResultDirectoryChanged(FileChoosePanelEvent event) {
        String chosenDirectory = event.getFileName();
        if (GUIConstants.EVENT_RESULT_DIRECTORY_CHOSE_PANEL.equals(event.getSource()) && chosenDirectory != null) {
            setChosenResultDirectory(chosenDirectory);
            KnownMetricFormatsHierarchicalOrganizer organizer = new KnownMetricFormatsHierarchicalOrganizer();
            Map<String, Object> metricHierarchy = organizer.organize(chosenDirectory, chosenDirectory + File.separator + Constants.REDUCERS_DUMP_DIRECTORY);
            ResultsMetricHierarchyAnalyzer analyzer = new ResultsMetricHierarchyAnalyzer(metricHierarchy);
            analyzer.calculateDerivatives();
            setResultsMetricHierarchyAnalyzer(analyzer);
            publishSelf();
        }
    }

    @Override
    public void publishSelf() {
        AnalyzePanelModelEvent event = new AnalyzePanelModelEvent(this);
        pubSub.publish(event);
    }
}
