package il.ac.sce.ir.metric.starter.gui.main.event.model_event;

import il.ac.sce.ir.metric.starter.gui.main.model.ReadabilitySelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class ReadabilitySelectionPanelModelEvent implements Event {

    private final ReadabilitySelectionPanelModel readabilitySelectionPanelModel;

    public ReadabilitySelectionPanelModel getReadabilitySelectionPanelModel() {
        return readabilitySelectionPanelModel;
    }

    public ReadabilitySelectionPanelModelEvent(ReadabilitySelectionPanelModel readabilitySelectionPanelModel) {
        this.readabilitySelectionPanelModel = readabilitySelectionPanelModel;
    }
}
