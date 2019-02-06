package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;

public class CategoryAnalyzePanel extends JPanel{

    private final PubSub pubSub;

    private final String category;

    private final ResultsMetricHierarchyAnalyzer resultsMetricHierarchyAnalyzer;

    public CategoryAnalyzePanel(PubSub pubSub, String category, ResultsMetricHierarchyAnalyzer resultsMetricHierarchyAnalyzer) {
        this.pubSub = pubSub;
        this.category = category;
        this.resultsMetricHierarchyAnalyzer = resultsMetricHierarchyAnalyzer;

        setLayout(new GridBagLayout());

        int y = 0;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = y++;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(new AnalyzeBySystemPanel(category, resultsMetricHierarchyAnalyzer), constraints);
        /*for (int i = 0; i < 1; i++) {
            constraints.gridx = 0;
            constraints.gridy = y++;
            add(new JButton("Test"), constraints);
        }*/

        y = 1000;
        WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
        GridBagConstraints springConstraints = wholeSpaceFiller.getFillingConstraints();
        springConstraints.gridy = y++;
        JPanel spring = new JPanel();
        add(spring, springConstraints);
    }

    public void subscribe() {

    }

    public void unsubscribe() {

    }

}
