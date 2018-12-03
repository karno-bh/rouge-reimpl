package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FileChoosePanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.MetricEnabledPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.ReadabilitySelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.RougeSelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;

public class MetricPanelModel implements AppModel {

    private final PubSub pubSub;

    private String chosenMetricsDirectory = null;

    private boolean rougeEnabled = false;

    private boolean readabilityEnabled = false;

    private boolean autoSummENGEnabled = false;

    public MetricPanelModel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(FileChoosePanelEvent.class, this::onMetricDirectoryChanged);
        pubSub.subscribe(MetricEnabledPanelEvent.class, this::onRougeMetricSelected);
    }

    public String getChosenMetricsDirectory() {
        return chosenMetricsDirectory;
    }

    public void setChosenMetricsDirectory(String chosenMetricsDirectory) {
        this.chosenMetricsDirectory = chosenMetricsDirectory;
    }

    public void setRougeEnabled(boolean rougeEnabled) {
        this.rougeEnabled = rougeEnabled;
    }

    public boolean isRougeEnabled() {
        return rougeEnabled;
    }

    public boolean isReadabilityEnabled() {
        return readabilityEnabled;
    }

    public void setReadabilityEnabled(boolean readabilityEnabled) {
        this.readabilityEnabled = readabilityEnabled;
    }

    public boolean isAutoSummENGEnabled() {
        return autoSummENGEnabled;
    }

    public void setAutoSummENGEnabled(boolean autoSummENGEnabled) {
        this.autoSummENGEnabled = autoSummENGEnabled;
    }

    private void onMetricDirectoryChanged(Event event) {
        FileChoosePanelEvent e = (FileChoosePanelEvent) event;
        if (GUIConstants.EVENT_WORKING_SET_DIRECTORY_CHOSE_PANEL.equals(e.getSource()) && e.getFileName() != null) {
            setChosenMetricsDirectory(e.getFileName());
            publishSelf();
        }
    }

    private void onRougeMetricSelected(Event event) {
        MetricEnabledPanelEvent e = (MetricEnabledPanelEvent) event;
        if (GUIConstants.EVENT_ROUGE_METRIC_SELECTED.equals(e.getName())) {
            setRougeEnabled(e.isSelected());
            publishSelf();
        } else if (GUIConstants.EVENT_READABILITY_METRIC_SELECTED.equals(e.getName())) {
            setReadabilityEnabled(e.isSelected());
            publishSelf();
        } else if (GUIConstants.EVENT_AUTO_SUMM_ENG_METRIC_SELECTED.equals(e.getName())) {
            setAutoSummENGEnabled(e.isSelected());
            publishSelf();
        }
    }

    @Override
    public void publishSelf() {
        MetricPanelModelChangedEvent changedEvent = new MetricPanelModelChangedEvent(this);
        pubSub.publish(changedEvent);
    }

}
