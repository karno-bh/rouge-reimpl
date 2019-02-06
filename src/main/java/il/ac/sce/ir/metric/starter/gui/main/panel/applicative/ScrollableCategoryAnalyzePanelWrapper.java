package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;

public class ScrollableCategoryAnalyzePanelWrapper extends JPanel{

    private final CategoryAnalyzePanel panel;

    public ScrollableCategoryAnalyzePanelWrapper(PubSub pubSub, String category, ResultsMetricHierarchyAnalyzer resultsMetricHierarchyAnalyzer) {
        this.panel = new CategoryAnalyzePanel(pubSub, category, resultsMetricHierarchyAnalyzer);
        setLayout(new GridBagLayout());
        WholeSpaceFiller filler = new WholeSpaceFiller();
        add(new JScrollPane(panel), filler.getFillingConstraints());
    }

    public void subscribe() {
        panel.subscribe();
    }

    public void unsubscribe() {
        panel.unsubscribe();
    }
}
