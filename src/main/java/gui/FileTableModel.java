package gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;

/**
 * Describes TableModel used to display contents of a file. The number
 * of columns is limited to 16.
 */
public class FileTableModel extends AbstractTableModel {

    /**
     * Stores the file data. First column is an offset column, others
     * are byte columns.
     */
    private final ArrayList<ArrayList<Object>> data = new ArrayList<>();

    /**
     * Returns the row count of the model.
     * @return the row count
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * Returns the column count of the model. The number of columns
     * is limited to 16.
     * @return the number 16
     */
    @Override
    public int getColumnCount() {
        return 16;
    }

    /**
     * Returns the value at the specified cell.
     * @param rowIndex        the row whose value is to be queried
     * @param columnIndex     the column whose value is to be queried
     * @return the Object value at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return data.get(rowIndex).get(columnIndex);
        }
        try {
            return String.format("%02X", data.get(rowIndex).get(columnIndex));
        } catch (IllegalFormatConversionException e) {
            return "";
        }
    }

    /**
     * Returns the class of the column.
     * @param columnIndex  the column being queried
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
     * @param column  the column being queried
     * @return column name
     */
    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Offset";
        }
        return String.format("%02X", column - 1);
    }

    /**
     * Fills the TableModel with the file data deleting previous.
     * @param fileData read file data
     */
    public void setDataSource(byte[] fileData) {
        int bytesToRead = fileData.length;
        int pos = 0;
        data.clear();

        while (bytesToRead > 0) {
            ArrayList<Object> row = new ArrayList<>();
            // Inserts into first column the offset value
            row.add(String.format("%08X", pos));

            for (int j = 0; j < getColumnCount(); j++) {
                try {
                    row.add(fileData[pos + j]);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    row.add("");
                }
            }

            pos += getColumnCount();
            bytesToRead -= getColumnCount();

            data.add(row);
        }
        fireTableStructureChanged();
    }
}
