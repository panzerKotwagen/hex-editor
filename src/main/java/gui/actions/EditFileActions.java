package gui.actions;

import editor.HexEditor;
import gui.tables.FileTable;
import gui.tables.FileTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The class that provides file editing operations (cut, copy, paste, etc.)
 * and static variables of the FileEditAction objects
 * for use them by menu and toolbar buttons.
 */
public class EditFileActions {
    public static EditFileAction cutAct;
    public static EditFileAction copyAct;
    public static EditFileAction pasteAct;

    /**
     * The table in which file data is displayed.
     */
    private static FileTable fileTable;

    /**
     * The table model of the FileTable.
     */
    private static FileTableModel tableModel;

    private static byte[] byteClipboard;

    private static HexEditor hexEditor;

    /**
     * The variable to indicate the position of the file starting
     * from which an operation will perform.
     */
    private static int offset;

    /**
     * Selected cell count.
     */
    private static int count;

    public EditFileActions() {
        makeEditFileActions();
    }

    public static void init(FileTable fileTable, FileTableModel tableModel, HexEditor hex) {
        EditFileActions.fileTable = fileTable;
        EditFileActions.tableModel = tableModel;
        EditFileActions.hexEditor = hex;
    }

    /**
     * Sets whether actions are enabled or not.
     * @param newValue true - to allow, false - to forbid
     */
    public static void unblockEditActions(boolean newValue) {
        copyAct.setEnabled(newValue);
        cutAct.setEnabled(newValue);
        pasteAct.setEnabled(newValue);
    }

    /**
     * Updates the offset of the selected byte in the table.
     */
    private static void updateSelectedByteOffset() {
        offset = tableModel.getIndex(fileTable.selectedRowIndexStart, fileTable.selectedColIndexStart);
        int end = tableModel.getIndex(fileTable.selectedRowIndexEnd, fileTable.selectedColIndexEnd);
        count = end - offset + 1;
    }

    /**
     * Cuts the selected byte block into clipboard.
     */
    private void cut() {
        updateSelectedByteOffset();
        copy();
        hexEditor.delete(offset, count);
        tableModel.updateModel();
    }

    /**
     * Copies the selected byte block into clipboard.
     */
    private void copy() {
        updateSelectedByteOffset();
        byteClipboard = new byte[count];
        for (int i = 0; i < count; i++) {
            byteClipboard[i] = tableModel.getValueByIndex(offset + i);
        }
    }

    /**
     * Pastes the byte block from the clipboard starting from the
     * selected cell with replacement.
     */
    private void insertFromClipboard() {
        insert(byteClipboard);
    }

    /**
     * Pastes the given byte block starting from the selected cell
     * with replacement.
     *
     * @param bytes byte block to be inserted
     */
    private void insert(byte[] bytes) {
        updateSelectedByteOffset();
        hexEditor.insert(offset, bytes);
        tableModel.updateModel();
    }

    /**
     * Inserts the byte block to the selected position with the rest
     * offset to the right.
     * @param bytes byte array to be added
     */
    private void add(byte[] bytes) {
        updateSelectedByteOffset();
        hexEditor.add(offset, bytes);
        tableModel.updateModel();
    }

    /**
     * Creates static action instances for using them by buttons.
     */
    private void makeEditFileActions() {
        cutAct = new EditFileAction(
                "Cut",
                KeyEvent.VK_B,
                KeyEvent.VK_B,
                "Cut the selected byte block and save it to clipboard.");
        copyAct = new EditFileAction(
                "Copy",
                KeyEvent.VK_C,
                KeyEvent.VK_C,
                "Copy the selected byte block to clipboard.");
        pasteAct = new EditFileAction(
                "Paste",
                KeyEvent.VK_V,
                KeyEvent.VK_V,
                "Insert the byte block saved in the clipboard.");

        // The functions are not available until a file is opened
        unblockEditActions(false);
    }

    /**
     * The class that provides filling in the table cell using a
     * keyboard.
     */
    public static class TableKeyboardInput extends KeyAdapter {
        /**
         * The number that is written to cell.
         */
        private final StringBuilder num = new StringBuilder();
        /**
         * The byte offset in the file.
         */
        private int prevOffset = offset;

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if ((KeyEvent.VK_A <= keyCode && keyCode <= KeyEvent.VK_F)
                    || (KeyEvent.VK_0 <= keyCode && keyCode <= KeyEvent.VK_9)) {
                updateSelectedByteOffset();
                hexEditor.insert(offset, getNum(e.getKeyChar()));
                tableModel.updateModel();
            }
        }

        /**
         * Gets the event char and constructs the number.
         *
         * @param c pressed event char
         * @return byte value
         */
        private byte getNum(char c) {
            // If another cell was selected
            if (prevOffset != offset) {
                num.delete(0, num.length());
                prevOffset = offset;
            }

            if (num.length() > 2) {
                num.delete(0, 1);
            }

            num.append(c);
            return (byte) Long.parseLong(num.toString(), 16);
        }
    }

    /**
     * The class describes Action performing file edit operations.
     */
    public class EditFileAction extends AbstractAction {
        public EditFileAction(String name, int mnemonicKey,
                              int accel, String tTip) {
            super(name);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel,
                    InputEvent.CTRL_DOWN_MASK));
            putValue(MNEMONIC_KEY, mnemonicKey);
            putValue(SHORT_DESCRIPTION, tTip);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String comStr = e.getActionCommand();

            switch (comStr) {
                case "Cut":
                    cut();
                    break;
                case "Copy":
                    copy();
                    break;
                case "Paste":
                    insertFromClipboard();
                    break;
            }
        }
    }
}
