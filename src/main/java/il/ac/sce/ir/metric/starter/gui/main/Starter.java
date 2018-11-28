package il.ac.sce.ir.metric.starter.gui.main;

import il.ac.sce.ir.metric.core.utils.switch_obj.SwitchObj;
import il.ac.sce.ir.metric.starter.gui.main.panel.FileChoosePanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.MetricPanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.ScrollableMetricPanelWrapper;
import il.ac.sce.ir.metric.starter.gui.main.resources.DefaultGUIMessages;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Map;

public class Starter {

    private final DefaultGUIMessages guiMessages;

    private final JFrame mainFrame;
    private final JList sectionsList;
    private final JScrollPane listScroll;
    private final JSplitPane mainSplit;
    private final JPanel mainRightPanel;
    private final JPanel runMetricPanel;
    private final JPanel analyzeResultPanel;


    public Starter(String[] args) {
        guiMessages = new DefaultGUIMessages();
        Map<String, String> messages = guiMessages.getMessages();
        mainFrame = new JFrame();
        mainFrame.setTitle(messages.get(GUIConstants.MAIN_TITLE_NAME));
        sectionsList = new JList(new Object[] {
                messages.get(GUIConstants.SECTION_RUN_METRICS),
                messages.get(GUIConstants.SECTION_ANALYZE_RESULTS)
        });
        sectionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsList.setVisibleRowCount(-1);
        sectionsList.setSelectedIndex(0);
        sectionsList.addListSelectionListener(this::sectionsListItemChanged);

        listScroll = new JScrollPane(sectionsList);

        mainRightPanel = new JPanel();
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, mainRightPanel);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setDividerLocation(200);

        Dimension listMinimumSpace = new Dimension(150, 400);
        listScroll.setPreferredSize(listMinimumSpace);
        Dimension mainRightPanelSpace = new Dimension(600, 400);
        mainRightPanel.setPreferredSize(mainRightPanelSpace);
        mainRightPanel.setLayout(new GridBagLayout());

        runMetricPanel = new ScrollableMetricPanelWrapper();

        analyzeResultPanel = new JPanel();
        analyzeResultPanel.add(new JButton("Bye"));
        WholeSpaceFiller spaceFiller = new WholeSpaceFiller();

        GridBagConstraints metricConstraints = spaceFiller.getFillingConstraints();
        mainRightPanel.add(runMetricPanel, metricConstraints);

        GridBagConstraints analyzeConstraints = spaceFiller.getFillingConstraints();
        mainRightPanel.add(analyzeResultPanel, analyzeConstraints);

        analyzeResultPanel.setVisible(false);
        mainFrame.add(mainSplit);

        // mainFrame.pack();
        mainFrame.setSize(new Dimension(900, 600));
        mainFrame.setMinimumSize(mainFrame.getSize());
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private void sectionsListItemChanged(ListSelectionEvent event) {
        boolean userFinishedSelection = !event.getValueIsAdjusting();
        if (userFinishedSelection) {
            String selectedValue = (String)sectionsList.getSelectedValue();
            Map<String, String> messages = guiMessages.getMessages();
            new SwitchObj<String>()
                    .on(messages.get(GUIConstants.SECTION_RUN_METRICS), this::runMetricChosen)
                    .on(messages.get(GUIConstants.SECTION_ANALYZE_RESULTS), this::analyzeResultsChosen)
                    .onDefault(() -> {throw new IllegalArgumentException("No such category: " + selectedValue);})
                    .doSwitch(selectedValue);
        }
    }

    private void runMetricChosen() {
        analyzeResultPanel.setVisible(false);
        runMetricPanel.setVisible(true);
    }

    private void analyzeResultsChosen() {
        runMetricPanel.setVisible(false);
        analyzeResultPanel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Starter(args));
    }
}
