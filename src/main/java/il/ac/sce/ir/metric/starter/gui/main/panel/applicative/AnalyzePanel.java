package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FileChoosePanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.AnalyzePanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.AnalyzePanelModel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.FileChoosePanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.NamedHeaderPanel;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;
import il.ac.sce.ir.metric.starter.gui.main.util.FullLineFiller;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyzePanel extends JPanel {

    private final PubSub pubSub;

    private final FileChoosePanel resultDirectoryChoosePanel;

    private final JPanel springPanel;

    private Map<String, ScrollableCategoryAnalyzePanelWrapper> categoryAnalyzePanelMap;

    // private final JButton barChartButton;

    public AnalyzePanel(PubSub pubSub, ModelsManager modelsManager) {

        this.pubSub = pubSub;
        AnalyzePanelModel analyzePanelModel = new AnalyzePanelModel(pubSub);
        pubSub.subscribe(AnalyzePanelModelEvent.class, this::onAnalyzePanelModelEvent);
        modelsManager.register(analyzePanelModel);
        this.resultDirectoryChoosePanel = new FileChoosePanel(pubSub, GUIConstants.EVENT_RESULT_DIRECTORY_CHOSE_PANEL, null);
        setLayout(new GridBagLayout());
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(emptyBorder);
        FullLineFiller lineFiller = new FullLineFiller();

        final Insets lineInsets = new Insets(0, 0, 20, 0);
        int y = 0;
        add(new NamedHeaderPanel("Results Directory"), lineFiller.fullLine(y++));
        GridBagConstraints fileLine = lineFiller.fullLine(y++);
        fileLine.insets = lineInsets;
        add(resultDirectoryChoosePanel, fileLine);

        y = 1000;
        WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
        GridBagConstraints spring = wholeSpaceFiller.getFillingConstraints();
        spring.gridy = y++;
        springPanel = new JPanel();
        springPanel.setLayout(new GridBagLayout());
        add(springPanel, spring);

        /*barChartButton = new JButton("Bar Chart");
        GridBagConstraints barChartConstraints = new GridBagConstraints();
        barChartConstraints.gridx = 0;
        barChartConstraints.gridy = y++;
        barChartConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        add(barChartButton, barChartConstraints);*/

    }

    public void onAnalyzePanelModelEvent(AnalyzePanelModelEvent event) {
        AnalyzePanelModel model = event.getModel();
        JTabbedPane categoriesTab = new JTabbedPane();
        ResultsMetricHierarchyAnalyzer resultsMetricHierarchyAnalyzer = model.getResultsMetricHierarchyAnalyzer();
        if (resultsMetricHierarchyAnalyzer == null) {
            return;
        }
        Map<String, Set<String>> availableSystems = resultsMetricHierarchyAnalyzer.getAvailableSystems();
        if (availableSystems == null || availableSystems.isEmpty()) {
            return;
        }
        if (categoryAnalyzePanelMap != null) {
            categoryAnalyzePanelMap.values().forEach(ScrollableCategoryAnalyzePanelWrapper::unsubscribe);
        }
        categoryAnalyzePanelMap = new HashMap<>();
        availableSystems.forEach((category, systems) -> {
            ScrollableCategoryAnalyzePanelWrapper panel = new ScrollableCategoryAnalyzePanelWrapper(pubSub, category, resultsMetricHierarchyAnalyzer);
            categoriesTab.addTab(category, panel);
            panel.subscribe();
        });
        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = filler.getFillingConstraints();
        springPanel.add(categoriesTab, fillingConstraints);
        springPanel.revalidate();
        springPanel.repaint();
    }

}
