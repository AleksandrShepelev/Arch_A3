package InstrumentationPackage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MaintenanceIndicator extends JFrame {

    private JTable _table;
    private static final int OFFLINE_CRITICAL_TIMEOUT = 5; // how many seconds is the critical maximum for devices to be offline

    public static final Object columnNames[] = {
        "Device ID",
        "Device Name",
        "Device Description",
        "Last online"
    };

    public MaintenanceIndicator() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.lightGray);

        Object rowData[][] = {};

        _table = new JTable(new DefaultTableModel(rowData, columnNames));
        _table.getColumnModel().getColumn(1).setPreferredWidth(50); // device name
        _table.getColumnModel().getColumn(2).setPreferredWidth(250); // device description
        _table.setDefaultRenderer(Object.class, new MyCellRenderer());

        JScrollPane scrollPane = new JScrollPane(_table);
        add(scrollPane, BorderLayout.CENTER);
        setSize(600, 300);
        setVisible(true);

        repaint();

    } // constructor

    public void setRows(java.util.List<String[]> rows) {
        try {
            DefaultTableModel tableModel = (DefaultTableModel)_table.getModel();
            tableModel.setRowCount(0); // clear
            rows.forEach(tableModel::addRow);
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
    }

    private class MyCellRenderer extends DefaultTableCellRenderer {

        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            final Component cellComponent = super.getTableCellRendererComponent(
                    table, value, isSelected,
                    hasFocus, row, column);

            Object val = table.getValueAt(row, columnNames.length - 1);
            String columnValue = val.toString();
            float lastOnline = Float.parseFloat(columnValue);
            if (lastOnline > OFFLINE_CRITICAL_TIMEOUT) {
                setBackground(Color.red);
            } else {
                setBackground(Color.green);
            }

            return cellComponent;
        }
    }

}