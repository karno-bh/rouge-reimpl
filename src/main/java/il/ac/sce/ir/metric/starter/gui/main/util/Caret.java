package il.ac.sce.ir.metric.starter.gui.main.util;

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getElementsInLine() {
        return elementsInLine;
    }
}
