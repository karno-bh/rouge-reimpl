package il.ac.sce.ir.metric.starter.gui.main.event.component_event;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class FilterSelectionPanelEvent implements Event {

    private final String filterName;

    private final boolean selected;

    public FilterSelectionPanelEvent(String filterName, boolean selected) {
        this.filterName = filterName;
        this.selected = selected;
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean isSelected() {
        return selected;
    }
}
