package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.results.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class AnalyzeMetricSaverPanel extends JPanel{

    private static final Logger log = LoggerFactory.getLogger(AnalyzeMetricSaverPanel.class);

    private final String category;

    private final ResultsMetricHierarchyAnalyzer analyzer;

    private final String resultDirectory;

    private final JButton saveAll;

    private final JButton saveAverageBySystem;

    public AnalyzeMetricSaverPanel(String category, ResultsMetricHierarchyAnalyzer analyzer, String resultDirectory) {
        this.category = category;
        this.analyzer = analyzer;
        this.resultDirectory = resultDirectory;

        setLayout(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Result Actions");
        setBorder(border);
        WholeSpaceFiller filler = new WholeSpaceFiller();
        add(new JPanel(), filler.getFillingConstraints());
        int x = 1;

        Insets buttonInsets = new Insets(20,0,0,10);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x++;
        constraints.insets = buttonInsets;
        saveAverageBySystem = new JButton("Save Systems Averages");
        saveAverageBySystem.addActionListener(this::onSaveBySystemClicked);
        add(saveAverageBySystem, constraints);

        constraints.insets.right = 0;
        constraints.gridx = x++;
        saveAll = new JButton("Save All");
        saveAll.addActionListener(this::onSaveAllClicked);
        add(saveAll, constraints);
    }

    private void onSaveBySystemClicked(ActionEvent event) {
        File file = Paths.get(resultDirectory, Constants.AVERAGES_BY_SYSTEMS_FILE_NAME).toFile();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, analyzer.getSystemAverages());
        } catch (IOException e) {
            log.error("Cannot save Averages File", e);
        }
    }

    private void onSaveAllClicked(ActionEvent event) {
        File file = Paths.get(resultDirectory, Constants.ALL_RESULT_JSON_FILE_NAME).toFile();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, analyzer.getOriginalMetricHierarchy());
        } catch (IOException e) {
            log.error("Cannot save Averages File", e);
        }
    }


}
