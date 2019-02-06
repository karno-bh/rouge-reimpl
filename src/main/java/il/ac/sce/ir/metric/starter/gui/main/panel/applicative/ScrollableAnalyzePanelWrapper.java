package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;

public class ScrollableAnalyzePanelWrapper extends JPanel {

    private final PubSub pubSub;
    private final AnalyzePanel analyzePanel;

    public ScrollableAnalyzePanelWrapper(PubSub pubSub, ModelsManager modelsManager) {
        this.pubSub = pubSub;
        this.analyzePanel = new AnalyzePanel(pubSub, modelsManager);
        setLayout(new GridBagLayout());
        WholeSpaceFiller spaceFiller = new WholeSpaceFiller();
        GridBagConstraints metricPanelConstraints = spaceFiller.getFillingConstraints();
        add(new JScrollPane(analyzePanel), metricPanelConstraints);
    }

}
