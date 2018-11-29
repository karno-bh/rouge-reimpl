package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.starter.gui.main.event.FileChoosePanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.MetricEnabledPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.pubsub.Event;
import il.ac.sce.ir.metric.starter.gui.main.pubsub.PubSub;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;

public class MetricPanelModel {

    private final PubSub pubSub;

    private String chosenMetricsDirectory = null;

    private boolean rougeEnabled = false;


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

    private void onMetricDirectoryChanged(Event event) {
        FileChoosePanelEvent e = (FileChoosePanelEvent) event;
        if (GUIConstants.EVENT_WORKING_SET_DIRECTORY_CHOSE_PANEL.equals(e.getSource()) && e.getFileName() != null) {
            setChosenMetricsDirectory(e.getFileName());
            MetricPanelModelChangedEvent changedEvent = new MetricPanelModelChangedEvent(this);
            pubSub.publish(changedEvent);
        }
    }

    private void onRougeMetricSelected(Event event) {
        MetricEnabledPanelEvent e = (MetricEnabledPanelEvent) event;
        if (GUIConstants.EVENT_ROUGE_METRIC_SELECTED.equals(e.getName())) {
            setRougeEnabled(e.isSelected());
            MetricPanelModelChangedEvent changedEvent = new MetricPanelModelChangedEvent(this);
            pubSub.publish(changedEvent);
        }
    }

}
