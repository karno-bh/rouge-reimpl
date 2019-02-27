package il.ac.sce.ir.metric.starter.gui.main.util;

import java.awt.*;

public class Caret {

    private int x, y, elementsInLine;

    public Caret(int x, int y, int elementsInLine) {
        this.x = x;
        this.y = y;
        this.elementsInLine = elementsInLine;
    }

    public void next() {
        x++;
        if (x % elementsInLine == 0) {
            x = 0;
            y++;
        }
    }

    public GridBagConstraints next(GridBagConstraints constraints) {
        next();
        return asGridBag(constraints);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getElementsInLine() {
        return elementsInLine;
    }

    public GridBagConstraints asGridBag() {
        return asGridBag(null);
    }

    public GridBagConstraints asGridBag(GridBagConstraints constraints) {
        if (constraints == null) {
            constraints = new GridBagConstraints();
        }
        constraints.gridx = x;
        constraints.gridy = y;
        return constraints;
    }
}
