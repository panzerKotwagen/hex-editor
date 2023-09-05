package gui;

import editor.ByteSequence;
import editor.HexEditor;

import javax.swing.*;
import java.awt.*;

/**
 * Describes the table that is designed to display the contents of
 * a file in binary format.
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

        this.setRowHeight(40);
        this.setIntercellSpacing(new Dimension(10, 10));
        this.setShowGrid(false);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        this.setCellSelectionEnabled(true);
        this.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.getColumnModel().setSelectionModel(new ColumnSelectionModel());

        setColumnsWidth();
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
     * Sets preferred width for every column.
     */
    public void setColumnsWidth() {
        this.getColumnModel().getColumn(0).setPreferredWidth(
                OFFSET_COLUMN_WIDTH);
        for (int i = 1; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setPreferredWidth(
                    BYTE_COLUMN_WIDTH);
        }
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

    //TODO: Rewrite javadoc
    /**
     * Creates the table with the opened file data.
     */
    public static FileTable createTable(HexEditor dataSource) {
        FileTableModel model = new FileTableModel(16);

        model.setDataSource(dataSource);

        FileTable table = new FileTable(model);

        table.updateTableView(800);

        return table;
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
                //TODO: Rewrite
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


