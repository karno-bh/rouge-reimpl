package il.ac.sce.ir.metric.starter.gui.main.event.model_event;

import il.ac.sce.ir.metric.starter.gui.main.model.FilterSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class FilterSelectionPanelModelEvent implements Event {

    private final FilterSelectionPanelModel model;

    public FilterSelectionPanelModel getModel() {
        return model;
    }

    public FilterSelectionPanelModelEvent(FilterSelectionPanelModel model) {
        this.model = model;
    }
}
