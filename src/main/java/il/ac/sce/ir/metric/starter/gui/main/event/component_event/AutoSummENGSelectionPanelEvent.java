package il.ac.sce.ir.metric.starter.gui.main.event.component_event;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class AutoSummENGSelectionPanelEvent implements Event {

    public enum SelectionType {
        WORD_N_GRAMS_SELECTED,
        CHARACTER_N_GRAMS_SELECTED,
        WORD_N_GRAMS_VALUES_CHANGED,
        CHARACTER_N_GRAMS_VALUES_CHANGED,
    }

    private SelectionType selectionType;

    private int min;

    private int max;

    private int dist;

    private boolean selectedValue;

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public boolean isSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(boolean selectedValue) {
        this.selectedValue = selectedValue;
    }

    public AutoSummENGSelectionPanelEvent(SelectionType selectionType, int min, int max, int dist) {
        this.selectionType = selectionType;
        this.min = min;
        this.max = max;
        this.dist = dist;
    }

    public AutoSummENGSelectionPanelEvent(SelectionType selectionType, boolean selectedValue) {
        this.selectionType = selectionType;
        this.selectedValue = selectedValue;
    }
}
