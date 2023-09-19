package gui.tables;

import editor.ByteSequence;
import editor.HexEditor;

import javax.swing.table.AbstractTableModel;

/**
 * Describes TableModel with dynamic column count provides displaying
 * contents of a file in binary format.
 */
public class HexTableModel extends AbstractTableModel {

    /**
     * The buffer size in which file data is saved.
     */
    private final int bufferSize = 1024;

    /**
     * The position of the current saved buffer in the file.
     */
    private int offset = 0;

    /**
     * The file to edit in binary format.
     */
    private HexEditor hexEditor;

    /**
     * The buffer to save a part of the file data.
     */
    private byte[] buffer;

    /**
     * The column count of the model.
     */
    private int columnCount;

    /**
     * Creates the TableModel with the specified column count
     * allocated for displaying bytes.
     * The first column is always assigned to the offset.
     *
     * @param byteColumnCount the byte column count of the model
     */
    public HexTableModel(int byteColumnCount) {
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
        if (hexEditor == null) {
            return 0;
        }
        return (int) Math.ceil(
                (double) hexEditor.getFileSize() / (getColumnCount() - 1));
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
     * cell. If there is no byte at the given position returns empty string.
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

        int index = getOffset(rowIndex, columnIndex);
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
     * Returns the name of the column. The first column corresponds
     * to the offset the rest are byte position.
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
     * Sets the binary file from which the data is taken.
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
     * The offset column is not editable.
     *
     * @param row    the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return true if the cell is editable
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Returns the byte offset in the file by row and column number
     * of the cell.
     *
     * @param rowIndex    row index of the cell
     * @param columnIndex column index of the cell
     * @return the byte offset
     */
    public int getOffset(int rowIndex, int columnIndex) {
        return rowIndex * (getColumnCount() - 1) + columnIndex - 1;
    }

    /**
     * Updates current visible model part.
     */
    public void updateModel() {
        buffer = hexEditor.read(offset, bufferSize);
        fireTableDataChanged();
    }

    /**
     * Returns the ByteSequence of length 8 which filling with the
     * bytes starting from the specified offset. If there are no
     * enough bytes to the right of the offset position then returns
     * sequence of less length.
     */
    public ByteSequence getByteSequence(long offset) {
        return new ByteSequence(hexEditor.read(offset, 8));
    }
}
