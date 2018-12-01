package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;

public class ReadabilitySelectionPanel extends JPanel {

    private final PubSub pubSub;


    public ReadabilitySelectionPanel(PubSub pubSub) {
        this.pubSub = pubSub;
    }
}
