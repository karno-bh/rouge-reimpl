package il.ac.sce.ir.metric.starter.gui.main.model;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FileChoosePanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.AnalyzePanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

public class AnalyzePanelModel implements AppModel {

    private final PubSub pubSub;

    private String chosenResultDirectory;


    public String getChosenResultDirectory() {
        return chosenResultDirectory;
    }

    public void setChosenResultDirectory(String chosenResultDirectory) {
        this.chosenResultDirectory = chosenResultDirectory;
    }

    public AnalyzePanelModel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(FileChoosePanelEvent.class, this::onResultDirectoryChanged);
    }

    private void onResultDirectoryChanged(FileChoosePanelEvent event) {
        if (GUIConstants.EVENT_RESULT_DIRECTORY_CHOSE_PANEL.equals(event.getSource()) && event.getFileName() != null) {
            setChosenResultDirectory(event.getFileName());
            publishSelf();
        }
    }

    @Override
    public void publishSelf() {
        AnalyzePanelModelEvent event = new AnalyzePanelModelEvent(this);
        pubSub.publish(event);
    }
}
