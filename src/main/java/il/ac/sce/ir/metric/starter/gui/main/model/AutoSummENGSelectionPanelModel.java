package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.NGramTextConfig;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.SimpleTextConfig;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.AutoSummENGSelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.AutoSummENGSelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

public class AutoSummENGSelectionPanelModel implements AppModel {

    private final PubSub pubSub;

    private boolean simpleTextConfigSelected;

    private boolean charNGramSelected;

    private SimpleTextConfig simpleTextConfig = new SimpleTextConfig(1,1,1);

    private NGramTextConfig nGramTextConfig = new NGramTextConfig(1,1,1);

    public SimpleTextConfig getSimpleTextConfig() {
        return simpleTextConfig;
    }

    public NGramTextConfig getnGramTextConfig() {
        return nGramTextConfig;
    }

    public boolean isSimpleTextConfigSelected() {
        return simpleTextConfigSelected;
    }

    public boolean isCharNGramSelected() {
        return charNGramSelected;
    }

    public AutoSummENGSelectionPanelModel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(AutoSummENGSelectionPanelEvent.class, this::onAutoSummENGSelectionPanelChanged);
    }

    private void onAutoSummENGSelectionPanelChanged(AutoSummENGSelectionPanelEvent event) {
        if (AutoSummENGSelectionPanelEvent.SelectionType.WORD_N_GRAMS_SELECTED.equals(event.getSelectionType())) {
            simpleTextConfigSelected = event.isSelectedValue();
            publishSelf();
        } else if (AutoSummENGSelectionPanelEvent.SelectionType.CHARACTER_N_GRAMS_SELECTED.equals(event.getSelectionType())) {
            charNGramSelected = event.isSelectedValue();
            publishSelf();
        } else if (AutoSummENGSelectionPanelEvent.SelectionType.WORD_N_GRAMS_VALUES_CHANGED.equals(event.getSelectionType())) {
            simpleTextConfig = new SimpleTextConfig.Builder()
                .wordMin(event.getMin())
                .wordMax(event.getMax())
                .wordDist(event.getDist())
                .build();
            publishSelf();
        } else if (AutoSummENGSelectionPanelEvent.SelectionType.CHARACTER_N_GRAMS_VALUES_CHANGED.equals(event.getSelectionType())) {
            nGramTextConfig = new NGramTextConfig.Builder()
                .charMin(event.getMin())
                .charMax(event.getMax())
                .charDist(event.getDist())
                .build();
            publishSelf();
        }
    }

    @Override
    public void publishSelf() {
        AutoSummENGSelectionPanelModelEvent event = new AutoSummENGSelectionPanelModelEvent(this);
        pubSub.publish(event);
    }
}
