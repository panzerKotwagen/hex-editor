package gui.tables;

import editor.ByteSequence;
import editor.HexEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * Describes the table to display the contents of a file in binary
 * format.
 */
public class FileTable extends JTable {

    private static final int OFFSET_COLUMN_WIDTH = 120;

    private static final int BYTE_COLUMN_WIDTH = 50;

    private static final int SCROLL_BAR_WIDTH = 20;

    /**
     * The index of the starting selected row.
     */
    public int selectedRowIndexStart;

    /**
     * The index of the ending selected row.
     */
    public int selectedRowIndexEnd;

    /**
     * The index of the starting selected column.
     */
    public int selectedColIndexStart;

    /**
     * The index of the ending selected column.
     */
    public int selectedColIndexEnd;

    /**
     * Constructs a FileTable that is initialized with tableModel as
     * the data model.
     *
     * @param tableModel the data model for the table
     */
    public FileTable(FileTableModel tableModel) {
        super(tableModel);
        setRowHeight(40);
        setIntercellSpacing(new Dimension(10, 10));
        setShowGrid(false);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(
           ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getColumnModel().setSelectionModel(new ColumnSelectionModel());
        setColumnsWidth();
        getTableHeader().setReorderingAllowed(false);
        setDefaultRenderer(Number.class, new CustomTableCellRenderer());
    }

    /**
     * Changes the table structure according to the width of the
     * application window.
     *
     * @param frameWidth the width of the app window
     */
    public void updateTableView(int frameWidth) {
        int columnCount = (frameWidth - OFFSET_COLUMN_WIDTH - SCROLL_BAR_WIDTH)
                / BYTE_COLUMN_WIDTH;

        if (columnCount < 16) {
            columnCount = 16;
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }

        FileTableModel tableModel = (FileTableModel) this.getModel();
        tableModel.setColumnCount(columnCount);
        setColumnsWidth();
    }

    /**
     * Sets preferred width for every column in a given table.
     */
    public static void setColumnsWidth(JTable table, int width) {
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }

    /**
     * Sets the specified column width for instance of the FileTable.
     */
    public void setColumnsWidth() {
        setColumnsWidth(this, BYTE_COLUMN_WIDTH);
        this.getColumnModel().getColumn(0).setPreferredWidth(
                OFFSET_COLUMN_WIDTH);
    }

    /**
     * Updates selected cell indexes.
     */
    public void updateSelectedCellIndex() {
        selectedRowIndexStart = getSelectedRow();
        selectedRowIndexEnd = getSelectionModel().getMaxSelectionIndex();
        selectedColIndexStart = getSelectedColumn();
        selectedColIndexEnd = getColumnModel().getSelectionModel()
                .getMaxSelectionIndex();
    }

    /**
     * Creates a table containing the data of the given file.
     */
    public static FileTable createTable(HexEditor dataSource) {
        FileTableModel model = new FileTableModel(16);
        model.setDataSource(dataSource);
        return new FileTable(model);
    }

    /**
     * The ListSelectionModel in which it is forbidden to select the
     * first column.
     */
    private static class ColumnSelectionModel
            extends DefaultListSelectionModel {
        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (index0 == 0) {
                if (index1 != 0) index0 = 1;
                else return;
            } else if (index1 == 0) return;
            super.setSelectionInterval(index0, index1);
        }
    }

    /**
     * Return the byte block starting from the
     * selected byte and 7 more to the right of it.
     *
     * @return the byte block of length 8 starting from the selected byte
     */
    public ByteSequence getByteSequence() {
        byte[] array = new byte[8];
        FileTableModel tableModel = (FileTableModel) this.getModel();

        for (int i = 0; i < 8; i++) {
            try {
                int index = tableModel.getIndex(
                        this.selectedRowIndexStart,
                        this.selectedColIndexStart);
                array[i] = tableModel.getValueByIndex(index);
            }
            // If the number of bytes in the file starting from the
            // selected position is less than 8
            catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        return new ByteSequence(array);
    }
}

class Render extends JLabel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return null;
    }
}

class CustomTableCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);

        FileTable fTable = (FileTable) table;
        fTable.updateSelectedCellIndex();

        if (isSelected)
            cell.setBackground(new Color(0xA2DEEB));
        else if (fTable.selectedRowIndexStart <= row && row < fTable.selectedRowIndexEnd)
            cell.setBackground(new Color(0xA2DEEB));
        else
            cell.setBackground(new Color(0xFFFFFF));

        return cell;
    }
}
