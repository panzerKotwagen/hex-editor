package gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;

/**
 * Describes TableModel with dynamic column count used to display
 * contents of a file. Stores the entire file content.
 */
public class FileTableModel extends AbstractTableModel {

    /**
     * Stores the file data. First column is an offset column, others
     * are byte columns.
     */
    private final ArrayList<Object> data = new ArrayList<>();

    /**
     * The column count of the TableModel.
     */
    private int columnCount;

    /**
     * Creates the TableModel with the specified column count.
     * The first column is always assigned to the offset.
     * @param columnCount - the column count of the model
     */
    public FileTableModel(int columnCount) {
        this.columnCount = columnCount;
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
     * Sets the column count of the TableModel. Also notifies all
     * listeners that the table's structure has changed.
     * @param columnCount - new column count.
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
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
            return data.get(rowIndex * getColumnCount());
        }
        try {
            return String.format("%02X", data.get(
                    rowIndex * getColumnCount() + columnIndex));
        } catch (IllegalFormatConversionException e) {
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

        while (bytesToRead > 0) {
            // Inserts into first column the offset value
            data.add(String.format("%08X", pos));

            for (int j = 0; j < getColumnCount() - 1; j++) {
                try {
                    data.add(fileData[pos + j]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    data.add("");
                }
            }

            pos += getColumnCount() - 1;
            bytesToRead -= getColumnCount() - 1;
        }
        fireTableStructureChanged();
    }
}
