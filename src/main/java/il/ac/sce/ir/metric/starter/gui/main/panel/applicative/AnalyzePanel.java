package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

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

public class AnalyzePanel extends JPanel {

    private final PubSub pubSub;

    private final FileChoosePanel resultDirectoryChoosePanel;

    private final JButton barChartButton;

    public AnalyzePanel(PubSub pubSub, ModelsManager modelsManager) {

        this.pubSub = pubSub;
        AnalyzePanelModel analyzePanelModel = new AnalyzePanelModel(pubSub);
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
        JPanel springPanel = new JPanel();
        add(springPanel, spring);

        barChartButton = new JButton("Bar Chart");
        GridBagConstraints barChartConstraints = new GridBagConstraints();
        barChartConstraints.gridx = 0;
        barChartConstraints.gridy = y++;
        barChartConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        add(barChartButton, barChartConstraints);

    }
}
