package gui;

import editor.HexEditor;

import javax.swing.table.AbstractTableModel;

/**
 * Describes TableModel with dynamic column count provides displaying
 * contents of a file in binary format.
 */
public class FileTableModel extends AbstractTableModel {
    private int offset = 0;
    private HexEditor hexEditor;
    private byte[] buffer;
    private final int bufferSize = 1024;

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
        if (hexEditor == null)
            return 0;
        return (int) Math.ceil((double) hexEditor.getFileSize() / (getColumnCount() - 1));
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
     * Returns the string represent of the byte value at the specified
     * cell. If there is no byte at the given position return empty string.
     * If the columnIndex equals to 0 return calculated offset
     * which value depends on current column count in the model.
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

        int index = getIndex(rowIndex, columnIndex);
        if (index >= bufferSize + offset || index < offset) {
            offset = index - index % bufferSize;
            buffer = hexEditor.read(offset, bufferSize);
        }

        try {
            return String.format("%02X", buffer[index - offset]);
        } catch (ArrayIndexOutOfBoundsException e) {
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
     * @param hex the file which data is read
     */
    public void setDataSource(HexEditor hex) {
        hexEditor = hex;
        buffer = hexEditor.read(0, bufferSize);
        fireTableStructureChanged();
    }

    /**
     * Returns true if the cell at row and column is editable.
     * Otherwise, invoking setValueAt on the cell will have no effect.
     *
     * @param row    the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return true if the cell is editable
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0)
            return false;

        return super.isCellEditable(row, column);
    }

    /**
     * Returns the byte value from model data at the specified index.
     *
     * @param index byte index
     * @return the byte value at the specified index
     */
    public byte getValueByIndex(int index) {
        return buffer[index - offset];
    }

    /**
     * Returns the byte index as if the whole file content
     * was stored in an array.
     * @param rowIndex row index of the cell
     * @param columnIndex column index of the cell
     * @return
     */
    public int getIndex(int rowIndex, int columnIndex) {
        return rowIndex * (getColumnCount() - 1) + columnIndex - 1;
    }

    /**
     * Updates current visible table part.
     */
    public void updateTable() {
        buffer = hexEditor.read(offset, bufferSize);
        fireTableDataChanged();
    }
}
