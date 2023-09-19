package gui.tables;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * The class that provides a limited-size table for entering a block
 * of bytes from the keyboard.
 */
public class InputTable extends JTable {

    /**
     * Constructs the table which can be used to writing bytes.
     */
    public InputTable() {
        super();
        InputTableModel model = new InputTableModel();
        this.setModel(model);
        this.setRowHeight(40);
        this.setIntercellSpacing(new Dimension(10, 10));
        this.setShowGrid(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setCellSelectionEnabled(false);
        HexTable.setColumnsWidth(this, 50);
        this.addKeyListener(new TableKeyboardInput(model));
        getTableHeader().setReorderingAllowed(false);
    }

    /**
     * Returns the recorded bytes as a byte array.
     * @return byte array
     */
    public byte[] getData() {
        ArrayList<Byte> data =((InputTableModel) this.getModel()).data;

        byte[] res = new byte[data.size()];

        int j = 0;
        for(Byte b: data)
            res[j++] = b;

        return res;
    }

    /**
     * The class that describes limited-size model for the InputTable.
     */
    private static class InputTableModel extends AbstractTableModel {

        static public final int ROW_COUNT = 4;
        static public final int COL_COUNT = 8;
        static public final int MAX_DATA_SIZE = ROW_COUNT * COL_COUNT;

        /**
         * The list to store inputted bytes.
         */
        ArrayList<Byte> data = new ArrayList<>();

        /**
         * Return fixed row count.
         */
        @Override
        public int getRowCount() {
            return ROW_COUNT;
        }

        /**
         * Return fixed column count.
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * Returns the bytes inputted by user as hex number in string
         * format. Returns "..." for the empty cells.
         * @param rowIndex        the row whose value is to be queried
         * @param columnIndex     the column whose value is to be queried
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                return String.format(
                        "%02X", data.get(getIndex(rowIndex, columnIndex)));
            } catch (IndexOutOfBoundsException e) {
                return "...";
            }
        }

        /**
         * All columns are designated as numeric.
         * @param columnIndex  the column being queried
         */
        @Override
        public Class<Number> getColumnClass(int columnIndex) {
            return Number.class;
        }

        /**
         * Return the column number.
         */
        @Override
        public String getColumnName(int columnIndex) {
            return String.format("%02X", columnIndex);
        }

        /**
         * It is the same as getting the index of a one-dimensional
         * array, which is represented as two-dimensional.
         */
        public int getIndex(int rowIndex, int columnIndex) {
            return rowIndex * (getColumnCount()) + columnIndex;
        }

        /**
         * Add the byte to model.
         * @param index data array index
         * @param value byte value
         */
        public void add(int index, Byte value) {
            try {
                data.set(index, value);
            }
            catch (IndexOutOfBoundsException e) {
                data.add(value);
            }
            fireTableDataChanged();
        }

        /**
         * Removes the last element from the data list.
         */
        public void pop() {
            if (data.isEmpty())
                return;
            data.remove(data.size() - 1);
            fireTableDataChanged();
        }
    }

    /**
     * The KeyListener for the keyboard input into the table.
     */
    private static class TableKeyboardInput extends KeyAdapter {

        /**
         * The number in hex format that is written to cell.
         */
        private final StringBuilder num = new StringBuilder();

        /**
         * The InputTableModel to edit.
         */
        InputTableModel model;

        /**
         * The number of recorded numbers. A number is considered
         * written if two digits have been entered.
         */
        private int count = 0;

        /**
         * Sets the table to edit.
         */
        public TableKeyboardInput(InputTableModel model) {
            this.model = model;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                delete();
            } else if ((KeyEvent.VK_A <= keyCode && keyCode <= KeyEvent.VK_F)
                    || (KeyEvent.VK_0 <= keyCode && keyCode <= KeyEvent.VK_9)
                    || KeyEvent.VK_NUMPAD0 <= keyCode
                    && keyCode <= KeyEvent.VK_NUMPAD9) {

                if (count < InputTableModel.MAX_DATA_SIZE)
                    append(e.getKeyChar());
            }
        }

        /**
         * Adds a number to the current cell. Two digits in hexadecimal
         * format are entered into each cell. When two numbers are
         * entered <code>count</code> is incremented.
         *
         * @param insertedChar char of the key that was pressed. It
         *                     is represented a number in hex format.
         */
        private void append(char insertedChar) {
            num.append(insertedChar);
            byte b = (byte) Long.parseLong(num.toString(), 16);
            model.add(count, b);

            if (num.length() == 2) {
                count += 1;
                num.delete(0, 2);
            }
        }

        /**
         * Erases the current cell. Decrements the <code>count</code>.
         */
        private void delete() {
            if (!model.data.isEmpty()) {
                model.pop();

                if (num.length() == 0)
                    count -= 1;

                num.delete(0, 2);
            }
        }
    }
}