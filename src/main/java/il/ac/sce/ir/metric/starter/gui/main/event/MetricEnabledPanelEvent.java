package il.ac.sce.ir.metric.starter.gui.main.event;

import il.ac.sce.ir.metric.starter.gui.main.pubsub.Event;

public class MetricEnabledPanelEvent implements Event{

    private final String name;
    private final boolean selected;

    public MetricEnabledPanelEvent(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("{ 'eventName': '").append("MetricEnabledPanelEvent', ")
                .append("'name': '").append(name).append(", ")
                .append("'selected': ").append(selected).append(" }");
        return sb.toString();
    }
}
