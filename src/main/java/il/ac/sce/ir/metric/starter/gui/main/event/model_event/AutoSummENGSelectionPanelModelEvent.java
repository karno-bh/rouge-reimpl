package il.ac.sce.ir.metric.starter.gui.main.event.model_event;

import il.ac.sce.ir.metric.starter.gui.main.model.AutoSummENGSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class AutoSummENGSelectionPanelModelEvent implements Event {

    private final AutoSummENGSelectionPanelModel model;

    public AutoSummENGSelectionPanelModel getModel() {
        return model;
    }

    public AutoSummENGSelectionPanelModelEvent(AutoSummENGSelectionPanelModel model) {
        this.model = model;
    }

}
