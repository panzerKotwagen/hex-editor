package gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Describes TableModel with dynamic column count used to display
 * contents of a file.
 */
public class FileTableModel extends AbstractTableModel {

    /**
     * Stores the file data.
     */
    private final ArrayList<Object> data = new ArrayList<>();

    /**
     * The column count of the TableModel.
     */
    private int columnCount;

    /**
     * Creates the TableModel with the specified column count
     * allocated for displaying bytes.
     * The first column is always assigned to the offset.
     *
     * @param byteColumnCount - the byte column count of the model
     */
    public FileTableModel(int byteColumnCount) {
        this.columnCount = byteColumnCount + 1;
    }

    /**
     * Returns the number of rows calculated from the number of
     * columns.
     *
     * @return the row count of the TableModel
     */
    @Override
    public int getRowCount() {
        return (int) Math.ceil((double) data.size() / getColumnCount());
    }

    /**
     * Returns the column count of the model.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Sets the column count allocated for displaying bytes of the
     * TableModel. Also notifies all listeners that the table's
     * structure has changed.
     *
     * @param byteColumnCount - new byte column count
     */
    public void setColumnCount(int byteColumnCount) {
        this.columnCount = byteColumnCount + 1;
        fireTableStructureChanged();
    }

    /**
     * Returns the value at the specified cell.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the Object value at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            // Return calculated offset
            return String.format("%08X", rowIndex * (getColumnCount() - 1));
        }
        try {
            // -1 is subtracted because offset is not stored in the data
            return String.format("%02X",
                    data.get(rowIndex * (getColumnCount() - 1) + columnIndex));
        } catch (IndexOutOfBoundsException e) {
            // If there is no bytes return empty string.
            // It is necessary to fill with empty strings those cells
            // of the last row for which there are not enough bytes.
            return "";
        }
    }

    /**
     * Returns the class of the column.
     *
     * @param columnIndex the column being queried
     * @return column class
     */
    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        }
        return Number.class;
    }

    /**
     * Returns the name of the column.
     *
     * @param columnIndex the column being queried
     * @return column name
     */
    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "Offset";
        }
        return String.format("%02X", columnIndex - 1);
    }

    /**
     * Fills the TableModel with the file data deleting previous.
     *
     * @param fileData read file data
     */
    public void setDataSource(byte[] fileData) {
        int bytesToRead = fileData.length;
        int pos = 0;
        data.clear();

        while (pos < bytesToRead) {
            data.add(fileData[pos++]);
        }

        fireTableStructureChanged();
    }
}
