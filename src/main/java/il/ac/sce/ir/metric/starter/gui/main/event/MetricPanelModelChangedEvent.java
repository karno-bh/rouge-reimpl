package il.ac.sce.ir.metric.starter.gui.main.event;

import il.ac.sce.ir.metric.starter.gui.main.model.MetricPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.pubsub.Event;

import java.util.Objects;

public class MetricPanelModelChangedEvent implements Event {

    private final MetricPanelModel metricPanelModel;

    public MetricPanelModel getMetricPanelModel() {
        return metricPanelModel;
    }

    public MetricPanelModelChangedEvent(MetricPanelModel metricPanelModel) {
        Objects.requireNonNull(metricPanelModel);
        this.metricPanelModel = metricPanelModel;
    }
}
