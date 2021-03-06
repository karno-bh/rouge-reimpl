package il.ac.sce.ir.metric.starter.gui.main.event.component_event;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class ReadabilitySelectionPanelEvent implements Event{

    public enum SelectionType {
        PEERS_SELECTED,
        TOPICS_SELECTED
    }

    private final SelectionType selectionType;

    private final boolean selectedValue;

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public boolean isSelectedValue() {
        return selectedValue;
    }

    public ReadabilitySelectionPanelEvent(SelectionType selectionType, boolean selectedValue) {
        this.selectionType = selectionType;
        this.selectedValue = selectedValue;
    }
}
