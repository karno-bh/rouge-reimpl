package il.ac.sce.ir.metric.starter.gui.main;

import il.ac.sce.ir.metric.core.utils.switch_obj.SwitchObj;
import il.ac.sce.ir.metric.starter.gui.main.resources.DefaultGUIMessages;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Map;

public class Starter {

    private DefaultGUIMessages guiMessages = new DefaultGUIMessages();

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

        runMetricPanel = new JPanel();
        runMetricPanel.add(new JButton("Hello"));

        analyzeResultPanel = new JPanel();
        analyzeResultPanel.add(new JButton("Bye"));

        mainRightPanel.add(runMetricPanel);
        mainRightPanel.add(analyzeResultPanel);
        analyzeResultPanel.setVisible(false);
        mainFrame.add(mainSplit);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.setMinimumSize(mainFrame.getSize());
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
