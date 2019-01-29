package il.ac.sce.ir.metric.temp_playing;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MyPlayingWithTables {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("My Playing with JTable");
            JTable t = new JTable(new MetricTableModel());
            t.setFillsViewportHeight(true);
            t.setAutoCreateRowSorter(true);
            t.setPreferredScrollableViewportSize(new Dimension(200, 300));

            f.add(new JScrollPane(t));
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }

    static class MetricTableModel extends AbstractTableModel {

        private ArrayList<String> columns;

        private Object[][] data;

        public MetricTableModel() {
            columns = new ArrayList<>();
            columns.addAll(Arrays.asList("System", "RougeN1 F1", "RougeS Recall"));

            data = new Object[][]{
                    {"System 01", 0.45d, 0.4833d},
                    {"System 02", 0.33d, 0.483242d},
                    {"System 03", 0.87d, 0.438d},
                    {"System 04", 0.23d, 0.3548d},
                    {"System 05", 0.14d, 0.1458d},
            };
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public String getColumnName(int column) {
            return columns.get(column);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
