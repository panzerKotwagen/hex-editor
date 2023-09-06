package gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
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
        this.setModel(new InputTableModel());
        this.setRowHeight(40);
        this.setIntercellSpacing(new Dimension(10, 10));
        this.setShowGrid(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        FileTable.setColumnsWidth(this);
        this.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.addKeyListener(new TableKeyboardInput(this));
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
     * The class that describes model for the InputTable.
     */
    private static class InputTableModel extends AbstractTableModel {
        //TODO: Add comments
        static public final int MAX_DATA_SIZE = 32;
        ArrayList<Byte> data = new ArrayList<>();

        @Override
        public int getRowCount() {
            return 4;
        }

        @Override
        public int getColumnCount() {
            return 8;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                return String.format("%02X", data.get(getIndex(rowIndex, columnIndex)));
            } catch (IndexOutOfBoundsException e) {
                return "";
            }
        }

        @Override
        public Class<Number> getColumnClass(int columnIndex) {
            return Number.class;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return String.format("%02X", columnIndex);
        }

        public int getIndex(int rowIndex, int columnIndex) {
            return rowIndex * (getColumnCount()) + columnIndex;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            int idx = getIndex(rowIndex, columnIndex);
            if (data.size() < idx + 1)
                if (data.size() < MAX_DATA_SIZE)
                    data.add(idx, (Byte) aValue);
                else
                    return;
            else
                data.set(idx, (Byte) aValue);
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
         * The number that is written to cell.
         */
        private final StringBuilder num = new StringBuilder();
        InputTable table;
        /**
         * The cell position in which the number to be written.
         */
        private int offset = 0;

        public TableKeyboardInput(InputTable table) {
            this.table = table;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                delete();
            } else if ((KeyEvent.VK_A <= keyCode && keyCode <= KeyEvent.VK_F)
                    || (KeyEvent.VK_0 <= keyCode && keyCode <= KeyEvent.VK_9)) {

                if (offset < InputTableModel.MAX_DATA_SIZE)
                    append(e.getKeyChar());
            }
        }

        /**
         * Adds a number to the current cell. Two digits in hexadecimal
         * format are entered into each cell. When two numbers are
         * entered offset starts to indicate the following.
         *
         * @param insertedChar char of the key that was pressed. It
         *                     is represented a number in hex format.
         */
        private void append(char insertedChar) {
            num.append(insertedChar);

            byte b = (byte) Long.parseLong(num.toString(), 16);

            table.setValueAt(b,
                    offset / table.getColumnCount(),
                    offset % table.getColumnCount());

            if (num.length() == 2) {
                num.delete(0, 2);
                offset += 1;
            }
        }

        /**
         * Erases the current cell.
         */
        private void delete() {
            if (offset > 0) {
                ((InputTableModel) table.getModel()).pop();

                if (num.length() == 0)
                    offset -= 1;

                num.delete(0, 2);
            }
        }
    }
}