package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.RougeSelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.RougeSelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import java.util.HashMap;
import java.util.Map;

public class RougeSelectionPanelModel implements AppModel {

    private final PubSub pubSub;

    private final Map<Integer, Boolean> selectedNGramMetrics;

    private boolean rougeL;

    private boolean rougeW;

    public Map<Integer, Boolean> getSelectedNGramMetrics() {
        return selectedNGramMetrics;
    }

    public RougeSelectionPanelModel(PubSub pubSub) {
        this.pubSub = pubSub;
        this.selectedNGramMetrics = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            selectedNGramMetrics.put(i, false);
        }

        pubSub.subscribe(RougeSelectionPanelEvent.class, this::onRougeSelectionPanelEvent);
    }

    private void onRougeSelectionPanelEvent(RougeSelectionPanelEvent e) {
        if (RougeSelectionPanelEvent.SelectionType.ROUGE_N_STATIC.equals(e.getSelectionType())) {
            selectedNGramMetrics.put(e.getRougeNStatic(), e.isRougeNStaticValue());
        } else if (RougeSelectionPanelEvent.SelectionType.ROUGE_N_TEXT.equals(e.getSelectionType())) {
            String freeText = e.getnGramFreeText();
            Map<Integer, Boolean> clone = new HashMap<>();
            for (int i = 1; i <= 3; i++) {
                clone.put(i, selectedNGramMetrics.get(i));
            }
            for (String possibleNumber : freeText.split(",")) {
                try {
                    int possibleMetric = Integer.parseInt(possibleNumber);
                    if (possibleMetric > 3) {
                        clone.put(possibleMetric, true);
                    }
                } catch (NumberFormatException ignore) {}
            }
            selectedNGramMetrics.clear();
            selectedNGramMetrics.putAll(clone);
        } else if (RougeSelectionPanelEvent.SelectionType.ROUGE_L.equals(e.getSelectionType())) {
            setRougeL(e.isRougeL());
        } else if (RougeSelectionPanelEvent.SelectionType.ROUGE_W.equals(e.getSelectionType())) {
            setRougeW(e.isRougeW());
        }

        // System.out.println("selectedNGramMetrics: " + selectedNGramMetrics);
        publishSelf();
    }

    @Override
    public void publishSelf() {
        RougeSelectionPanelModelEvent modelEvent = new RougeSelectionPanelModelEvent(this);
        pubSub.publish(modelEvent);
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
