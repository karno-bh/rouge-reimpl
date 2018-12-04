package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.starter.gui.main.event.model_event.*;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import java.io.File;
import java.util.Map;

public class GoButtonModel implements AppModel {

    private final PubSub pubSub;

    private boolean goButtonEnabled;

    private MetricPanelModel metricPanelModel;

    private RougeSelectionPanelModel rougeSelectionPanelModel;

    private ReadabilitySelectionPanelModel readabilitySelectionPanelModel;

    private AutoSummENGSelectionPanelModel autoSummENGSelectionPanelModel;

    public boolean isGoButtonEnabled() {
        return goButtonEnabled;
    }

    public void setGoButtonEnabled(boolean goButtonEnabled) {
        this.goButtonEnabled = goButtonEnabled;
    }

    public GoButtonModel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricPanelModelChangedEvent);
        pubSub.subscribe(RougeSelectionPanelModelEvent.class, this::onRougeSelectionPanelModelEvent);
        pubSub.subscribe(ReadabilitySelectionPanelModelEvent.class, this::onReadabilitySelectionPanelModelEvent);
        pubSub.subscribe(AutoSummENGSelectionPanelModelEvent.class, this::onAutoSummENGSelectionPanelModelEvent);
    }

    private void onMetricPanelModelChangedEvent(MetricPanelModelChangedEvent event) {
        metricPanelModel = event.getMetricPanelModel();
        checkEnablement();
    }

    private void onRougeSelectionPanelModelEvent(RougeSelectionPanelModelEvent event) {
        rougeSelectionPanelModel = event.getModel();
        checkEnablement();
    }

    private void onReadabilitySelectionPanelModelEvent(ReadabilitySelectionPanelModelEvent event) {
        readabilitySelectionPanelModel = event.getReadabilitySelectionPanelModel();
        checkEnablement();
    }

    private void onAutoSummENGSelectionPanelModelEvent(AutoSummENGSelectionPanelModelEvent event) {
        autoSummENGSelectionPanelModel = event.getModel();
        checkEnablement();
    }

    private void checkEnablement() {
        String dirName = metricPanelModel.getChosenMetricsDirectory();
        boolean goButtonEnabled = false;
        if (dirName != null) {
            File dir = new File(dirName);
            if (dir.isDirectory()) {
                if (metricPanelModel.isRougeEnabled()) {
                    if (rougeSelectionPanelModel.isRougeL() || rougeSelectionPanelModel.isRougeW()) {
                        goButtonEnabled = true;
                    } else {
                        for (Map.Entry<Integer, Boolean> rougeNEnablement : rougeSelectionPanelModel.getSelectedNGramMetrics().entrySet()) {
                            if (rougeNEnablement.getValue() != null && rougeNEnablement.getValue()) {
                                goButtonEnabled = true;
                                break;
                            }
                        }
                    }
                }
                if (metricPanelModel.isReadabilityEnabled()) {
                    if (readabilitySelectionPanelModel.isPeersEnabled() || readabilitySelectionPanelModel.isTopicsEnabled()) {
                        goButtonEnabled = true;
                    }
                }
                if (metricPanelModel.isAutoSummENGEnabled()) {
                    if (autoSummENGSelectionPanelModel.isSimpleTextConfigSelected() || autoSummENGSelectionPanelModel.isCharNGramSelected()) {
                        goButtonEnabled = true;
                    }
                }
                // goButtonEnabled = true;
            }
        }
        //this.goButtonEnabled = goButtonEnabled;
        setGoButtonEnabled(goButtonEnabled);
        publishSelf();
    }



    @Override
    public void publishSelf() {
        GoButtonModelChangedEvent event = new GoButtonModelChangedEvent(this);
        pubSub.publish(event);
    }
}
