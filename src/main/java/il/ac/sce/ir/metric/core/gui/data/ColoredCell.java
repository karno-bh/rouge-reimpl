package il.ac.sce.ir.metric.core.gui.data;

import java.awt.*;

public class ColoredCell {

    private double value;

    private Color color;

    private int tableIndex;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }
}
