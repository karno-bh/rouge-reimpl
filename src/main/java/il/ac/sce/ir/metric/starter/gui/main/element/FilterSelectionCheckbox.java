package il.ac.sce.ir.metric.starter.gui.main.element;

import javax.swing.*;

public class FilterSelectionCheckbox extends JCheckBox {

    private final String filter;

    public FilterSelectionCheckbox(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }
}
