package il.ac.sce.ir.metric.starter.gui.main.event.model_event;

import il.ac.sce.ir.metric.starter.gui.main.model.AnalyzePanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class AnalyzePanelModelEvent implements Event {

    private final AnalyzePanelModel model;

    public AnalyzePanelModelEvent(AnalyzePanelModel model) {
        this.model = model;
    }

    public AnalyzePanelModel getModel() {
        return model;
    }
}
