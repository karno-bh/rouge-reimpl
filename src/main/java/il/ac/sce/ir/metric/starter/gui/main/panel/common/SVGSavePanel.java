package il.ac.sce.ir.metric.starter.gui.main.panel.common;

import il.ac.sce.ir.metric.starter.gui.main.util.Caret;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;

public class SVGSavePanel extends JPanel {

    private final JLabel svgFileNameLabel = new JLabel("SVG File Name");

    private final JLabel widthLabel = new JLabel("Width");

    private final JLabel heightLabel = new JLabel("Height");

    private final JTextField svgFileNameTextField = new JTextField();

    private final JTextField svgWidthTextField = new JTextField();

    private final JTextField svgHeightTextField = new JTextField();

    private final JCheckBox saveToSVGCheckBox = new JCheckBox();

    private List<JComponent> svgRelatedComponents;

    private boolean saveToSVG;

    public SVGSavePanel() {
        svgRelatedComponents = Arrays.asList(
                svgFileNameLabel, widthLabel, heightLabel,
                svgFileNameTextField, svgWidthTextField, svgHeightTextField
        );
        svgRelatedComponents.forEach(c -> c.setEnabled(false));

        setBorder(BorderFactory.createTitledBorder("Chart/Graph to File"));
        setLayout(new GridBagLayout());
        Caret caret = new Caret(0,0, 2);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(0, 10, 0,0);

        constraints = caret.asGridBag(constraints);
        add(svgFileNameLabel, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgFileNameTextField.setColumns(15);
        svgFileNameTextField.setToolTipText("The file will be saved in chosen result directory");
        add(svgFileNameTextField, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        add(widthLabel, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgWidthTextField.setColumns(15);
        add(svgWidthTextField, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        add(heightLabel, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgHeightTextField.setColumns(15);
        add(svgHeightTextField, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        JLabel saveOnOpen = new JLabel("Save SVG on Char/Graph Open");
        add(saveOnOpen, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);

        saveToSVGCheckBox.addItemListener(this::onSaveToSVGCheckBoxChanged);
        constraints.anchor = GridBagConstraints.LINE_START;
        add(saveToSVGCheckBox, constraints);
    }

    private void onSaveToSVGCheckBoxChanged(ItemEvent event) {
        saveToSVG = event.getStateChange() == ItemEvent.SELECTED;
        svgRelatedComponents.forEach(c -> c.setEnabled(saveToSVG));
    }

    public JLabel getSvgFileNameLabel() {
        return svgFileNameLabel;
    }

    public JLabel getWidthLabel() {
        return widthLabel;
    }

    public JLabel getHeightLabel() {
        return heightLabel;
    }

    public JTextField getSvgFileNameTextField() {
        return svgFileNameTextField;
    }

    public JTextField getSvgWidthTextField() {
        return svgWidthTextField;
    }

    public JTextField getSvgHeightTextField() {
        return svgHeightTextField;
    }

    public JCheckBox getSaveToSVGCheckBox() {
        return saveToSVGCheckBox;
    }

    public List<JComponent> getSvgRelatedComponents() {
        return svgRelatedComponents;
    }

    public boolean isSaveToSVG() {
        return saveToSVG;
    }
}
