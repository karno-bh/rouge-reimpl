package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.ReadabilitySelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.ReadabilitySelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

public class ReadabilitySelectionPanelModel implements AppModel {

    private final PubSub pubSub;

    private boolean peersEnabled;

    private boolean topicsEnabled;

    public ReadabilitySelectionPanelModel(PubSub pubSub) {
        this.pubSub = pubSub;
        pubSub.subscribe(ReadabilitySelectionPanelEvent.class, this::onReadabilitySelectionPanelEvent);
    }

    public boolean isPeersEnabled() {
        return peersEnabled;
    }

    public void setPeersEnabled(boolean peersEnabled) {
        this.peersEnabled = peersEnabled;
    }

    public boolean isTopicsEnabled() {
        return topicsEnabled;
    }

    public void setTopicsEnabled(boolean topicsEnabled) {
        this.topicsEnabled = topicsEnabled;
    }

    private void onReadabilitySelectionPanelEvent(ReadabilitySelectionPanelEvent event) {
        ReadabilitySelectionPanelEvent.SelectionType selectionType = event.getSelectionType();
        if (ReadabilitySelectionPanelEvent.SelectionType.PEERS_SELECTED.equals(selectionType)) {
            setPeersEnabled(event.isSelectedValue());
            publishSelf();
        } else if (ReadabilitySelectionPanelEvent.SelectionType.TOPICS_SELECTED.equals(selectionType)) {
            setTopicsEnabled(event.isSelectedValue());
            publishSelf();
        }
    }

    @Override
    public void publishSelf() {
        ReadabilitySelectionPanelModelEvent modelEvent = new ReadabilitySelectionPanelModelEvent(this);
        pubSub.publish(modelEvent);
    }
}
