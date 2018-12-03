package il.ac.sce.ir.metric.starter.gui.main.event.model_event;

import il.ac.sce.ir.metric.starter.gui.main.model.GoButtonModel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class GoButtonModelChangedEvent implements Event {

    private final GoButtonModel goButtonModel;

    public GoButtonModel getGoButtonModel() {
        return goButtonModel;
    }

    public GoButtonModelChangedEvent(GoButtonModel goButtonModel) {
        this.goButtonModel = goButtonModel;
    }
}
