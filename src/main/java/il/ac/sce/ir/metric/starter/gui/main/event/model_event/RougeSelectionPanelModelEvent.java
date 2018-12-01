package il.ac.sce.ir.metric.starter.gui.main.event.model_event;

import il.ac.sce.ir.metric.starter.gui.main.model.RougeSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

import java.util.Objects;

public class RougeSelectionPanelModelEvent implements Event {

    private final RougeSelectionPanelModel model;

    public RougeSelectionPanelModel getModel() {
        return model;
    }

    public RougeSelectionPanelModelEvent(RougeSelectionPanelModel model) {
        Objects.requireNonNull(model);
        this.model = model;
    }
}
