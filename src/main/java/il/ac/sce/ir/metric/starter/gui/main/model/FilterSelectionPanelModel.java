package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FilterSelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.FilterSelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import java.util.HashMap;
import java.util.Map;

public class FilterSelectionPanelModel implements AppModel{

    private final PubSub pubSub;

    private final Map<String, Boolean> selections = new HashMap<>();

    public Map<String, Boolean> getSelections() {
        return selections;
    }

    public FilterSelectionPanelModel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(FilterSelectionPanelEvent.class, this::onSelectionChanged);
    }

    public void onSelectionChanged(FilterSelectionPanelEvent event) {
        selections.put(event.getFilterName(), event.isSelected());
    }

    @Override
    public void publishSelf() {
        FilterSelectionPanelModelEvent event = new FilterSelectionPanelModelEvent(this);
        pubSub.publish(event);
    }
}
