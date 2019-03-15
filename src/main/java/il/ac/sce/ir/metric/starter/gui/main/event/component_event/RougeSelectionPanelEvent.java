package il.ac.sce.ir.metric.starter.gui.main.event.component_event;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class RougeSelectionPanelEvent implements Event {

    public enum SelectionType {
        ROUGE_N_STATIC,
        ROUGE_N_TEXT,
        ROUGE_S,
        ROUGE_S_UNIGRAMS,
        ROUGE_S_SKIP_DISTANCE,
        ROUGE_L,
        ROUGE_W,
    }

    private SelectionType selectionType;

    private int rougeNStatic;

    private boolean rougeNStaticValue;

    private boolean rougeS;

    private boolean rougeSUseUnigrams;

    private int skipDistance;

    private boolean rougeL;

    private boolean rougeW;

    private String nGramFreeText;

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public int getRougeNStatic() {
        return rougeNStatic;
    }

    public void setRougeNStatic(int rougeNStatic) {
        this.rougeNStatic = rougeNStatic;
    }

    public String getnGramFreeText() {
        return nGramFreeText;
    }

    public void setnGramFreeText(String nGramFreeText) {
        this.nGramFreeText = nGramFreeText;
    }

    public boolean isRougeNStaticValue() {
        return rougeNStaticValue;
    }

    public void setRougeNStaticValue(boolean rougeNStaticValue) {
        this.rougeNStaticValue = rougeNStaticValue;
    }

    public boolean isRougeS() {
        return rougeS;
    }

    public void setRougeS(boolean rougeS) {
        this.rougeS = rougeS;
    }

    public boolean isRougeSUseUnigrams() {
        return rougeSUseUnigrams;
    }

    public void setRougeSUseUnigrams(boolean rougeSUseUnigrams) {
        this.rougeSUseUnigrams = rougeSUseUnigrams;
    }

    public int getSkipDistance() {
        return skipDistance;
    }

    public void setSkipDistance(int skipDistance) {
        this.skipDistance = skipDistance;
    }

    public boolean isRougeL() {
        return rougeL;
    }

    public void setRougeL(boolean rougeL) {
        this.rougeL = rougeL;
    }

    public boolean isRougeW() {
        return rougeW;
    }

    public void setRougeW(boolean rougeW) {
        this.rougeW = rougeW;
    }
}
