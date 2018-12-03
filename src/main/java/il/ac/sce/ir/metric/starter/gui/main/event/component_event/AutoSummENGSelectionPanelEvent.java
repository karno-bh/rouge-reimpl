package il.ac.sce.ir.metric.starter.gui.main.event.component_event;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class AutoSummENGSelectionPanelEvent implements Event {

    public enum SelectionType {
        WORD_N_GRAMS_SELECTED,
        CHARACTER_N_GRAMS_SELECTED
    }

    private final SelectionType selectionType;

    private final int min;

    private final int max;

    private final int dist;

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getDist() {
        return dist;
    }

    public AutoSummENGSelectionPanelEvent(SelectionType selectionType, int min, int max, int dist) {
        this.selectionType = selectionType;
        this.min = min;
        this.max = max;
        this.dist = dist;
    }
}
