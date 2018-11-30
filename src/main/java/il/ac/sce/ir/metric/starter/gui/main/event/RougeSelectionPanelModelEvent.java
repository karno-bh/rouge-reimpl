package il.ac.sce.ir.metric.starter.gui.main.event;

import il.ac.sce.ir.metric.starter.gui.main.model.RougeSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.pubsub.Event;

public class RougeSelectionPanelModelEvent implements Event {

    private final RougeSelectionPanelModel model;

    public RougeSelectionPanelModel getModel() {
        return model;
    }

    public RougeSelectionPanelModelEvent(RougeSelectionPanelModel model) {
        this.model = model;
    }
}
