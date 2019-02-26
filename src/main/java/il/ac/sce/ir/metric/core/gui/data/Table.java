package il.ac.sce.ir.metric.core.gui.data;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class Table extends AbstractTableModel {

    private List<String> columns;

    private List<List<Object>> data = new ArrayList<>();

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public void addRow(List<Object> row) {
        data.add(row);
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
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
        try {
            return data.get(rowIndex).get(columnIndex);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<List<Object>> getData() {
        return data;
    }

    public List<String> getColumns() {
        return columns;
    }
}
