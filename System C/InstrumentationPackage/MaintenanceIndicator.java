/******************************************************************************************************************
 * File:Indicator.java
 * Course: 17655
 * Project: Assignment A3
 * Copyright: Copyright (c) 2009 Carnegie Mellon University
 * Versions:
 * 1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
 * <p>
 * Description:
 * <p>
 * This class is used to create an indicator lamp on the terminal. The indicator lamp is essentiall a square box
 * with a round corner rectangle inside that is the indicator lamp. The lamp's color can be turn black, green,
 * yellow, or red. A short message can be displayed below the indicator lamp as well. Both the lamp color and the
 * message can be changed at run time.
 * <p>
 * <p>
 * Parameters: SEE THE CONSTRUCTOR BELOW
 * <p>
 * Internal Methods:
 * <p>
 * public int GetX
 * public int GetY
 * public int Height()
 * public int Width()
 * public void SetLampColorAndMessage(String message, int color)
 * public void SetMessage( String message )
 ******************************************************************************************************************/
package InstrumentationPackage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MaintenanceIndicator extends JFrame {

    private JTable _table;

    /***************************************************************************
     * Constructor:: Indicator
     * Purpose: This method sets up a JFrame window and drawing pane with the
     *		   title specified at the position indicated by the x, y coordinates.
     *
     * Arguments: String Label - the indicator title
     *			 Float Xpos - the vertical position of the indicator on the screen
     *			 			  specified in terms of a percentage of the screen width.
     *			 Float Ypos - the horizontal position of the indicator on the screen
     *			 		 	  specified in terms of a percentage of the screen height.
     *
     * Returns: Indicator
     *
     * Exceptions: none
     *
     ****************************************************************************/

    public MaintenanceIndicator() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.lightGray);

        Object rowData[][] = {};
        Object columnNames[] = {"Device ID", "Device Type", "Device Name", "Last online"};
        _table = new JTable(new DefaultTableModel(rowData, columnNames));
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

            final java.awt.Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            //if (column == 3) {
                Object val = table.getValueAt(row, 3);
                String columnValue = val.toString();
                float lastOnline = Float.parseFloat(columnValue);
                if (lastOnline > 5) {
                    setBackground(Color.red);
                } else {
                    setBackground(Color.green);
                }
            //}

            return cellComponent;
        }
    }

} // Indicator