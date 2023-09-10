package gui.actions;

import editor.HexEditor;
import gui.tables.FileTable;
import gui.tables.FileTableModel;
import gui.window.MainWindow;
import gui.dialog.windows.InputDialogWindow;

import javax.swing.*;
import java.awt.*;
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
    public static EditFileAction addAct;
    public static EditFileAction insertAct;
    public static EditFileAction findAct;

    /**
     * The main application window.
     */
    private static MainWindow frame;

    /**
     * The table in which file data is displayed.
     */
    private static FileTable fileTable;

    /**
     * The table model of the FileTable.
     */
    private static FileTableModel tableModel;

    /**
     * The clipboard in which bytes are saved after cut and copy
     * operations.
     */
    private static byte[] byteClipboard;

    /**
     * The file to edit.
     */
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

    /**
     * Constructs the EditFileAction objects.
     */
    public EditFileActions() {
        makeEditFileActions();
    }

    /**
     * Initializes the static variables.
     *
     * @param fileTable  the table with file data
     * @param tableModel the model of the table
     * @param hex        the file to edit
     * @param win        the main application window
     */
    public static void init(FileTable fileTable, FileTableModel tableModel, HexEditor hex, MainWindow win) {
        EditFileActions.fileTable = fileTable;
        EditFileActions.tableModel = tableModel;
        EditFileActions.hexEditor = hex;
        EditFileActions.frame = win;
    }

    /**
     * Sets whether actions are enabled or not.
     *
     * @param newValue true - to allow, false - to forbid
     */
    public static void unblockEditActions(boolean newValue) {
        copyAct.setEnabled(newValue);
        cutAct.setEnabled(newValue);
        pasteAct.setEnabled(newValue);
        insertAct.setEnabled(newValue);
        addAct.setEnabled(newValue);
        findAct.setEnabled(newValue);
    }

    /**
     * Updates the offset and count of the selected bytes in the
     * table.
     */
    private static void updateSelectedByteOffset() {
        offset = tableModel.getIndex(fileTable.selectedRowIndexMin, fileTable.selectedColIndexMin);
        int end = tableModel.getIndex(fileTable.selectedRowIndexMax, fileTable.selectedColIndexMax);
        count = end - offset + 1;
    }

    /**
     * Creates static Action instances for using them by buttons.
     */
    private void makeEditFileActions() {
        cutAct = new EditFileAction(
                "Cut",
                KeyEvent.VK_B,
                KeyEvent.VK_B,
                "Cut the selected and save it on the clipboard.");
        copyAct = new EditFileAction(
                "Copy",
                KeyEvent.VK_C,
                KeyEvent.VK_C,
                "Copy the selected on the clipboard.");
        pasteAct = new EditFileAction(
                "Paste",
                KeyEvent.VK_V,
                KeyEvent.VK_V,
                "Paste block byte on the clipboard to file.");
        insertAct = new EditFileAction(
                "Insert",
                KeyEvent.VK_I,
                KeyEvent.VK_I,
                "Insert arbitrary bytes into file.");
        addAct = new EditFileAction(
                "Add",
                KeyEvent.VK_A,
                KeyEvent.VK_A,
                "Add arbitrary bytes into file.");
        findAct = new EditFileAction(
                "Find",
                KeyEvent.VK_F,
                KeyEvent.VK_F,
                "Find a byte mask in the file.");

        copyAct.putValue(
                AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK)
        );
        cutAct.putValue(
                AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK)
        );
        pasteAct.putValue(
                AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK)
        );

        // The functions are not available until a file is opened
        unblockEditActions(false);
    }

    /**
     * Cuts the selected byte block onto clipboard.
     */
    private void cut() {
        updateSelectedByteOffset();
        if (copy()) {
            hexEditor.delete(offset, count);
            tableModel.updateModel();
        }
    }

    /**
     * Copies the selected byte block onto clipboard.
     */
    private boolean copy() {
        updateSelectedByteOffset();
        byteClipboard = new byte[count];
        for (int i = 0; i < count; i++) {
            try {
                byteClipboard[i] = tableModel.getValueByIndex(offset + i);
            }
            catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                byteClipboard = new byte[0];
                return false;
            }
        }
        return true;
    }

    /**
     * Pastes the byte block from the clipboard starting from the
     * selected cell with replacement.
     */
    private void paste() {
        updateSelectedByteOffset();
        hexEditor.insert(offset, byteClipboard);
        tableModel.updateModel();
    }

    /**
     * Opens the dialog box for entering bytes inserted into the
     * selected position with the replacement.
     */
    private void insert() {
        updateSelectedByteOffset();
        InputDialogWindow win = new InputDialogWindow(frame, "Insert");
        byte[] bytes = win.getData();

        if (bytes == null || bytes.length == 0)
            return;

        hexEditor.insert(offset, bytes);
        tableModel.updateModel();
    }

    /**
     * Opens the dialog box for entering bytes inserted into the
     * selected position with the rest offset to the right.
     */
    private void add() {
        updateSelectedByteOffset();
        InputDialogWindow win = new InputDialogWindow(frame, "Add");
        byte[] bytes = win.getData();

        if (bytes == null || bytes.length == 0)
            return;

        hexEditor.add(offset, bytes);
        tableModel.updateModel();
    }

    /**
     * Opens the dialog box for entering bytes to search in the file.
     */
    private void find() {
        InputDialogWindow win = new InputDialogWindow(frame, "Find");
        byte[] bytes = win.getData();

        if (bytes == null || bytes.length == 0)
            return;

        long res = hexEditor.find(0, bytes);

        if (res == -1) {
            JOptionPane.showMessageDialog(
                    frame, "The specified byte mask has not been found.");
            return;
        }

        // Get the row number
        res = res / (tableModel.getColumnCount() - 1);

        Adjustable e = frame.fileViewPanel.getVerticalScrollBar();

        // One line corresponds to the 40 value of the scrollbar.
        e.setValue((int) res * 40);

        frame.updateFrame();
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

            // if user is using hotkeys
            if (e.isControlDown() || e.isAltDown())
                return;

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
                    InputEvent.CTRL_MASK));
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
                    paste();
                    break;
                case "Insert":
                    insert();
                    break;
                case "Add":
                    add();
                    break;
                case "Find":
                    find();
                    break;
            }
        }
    }
}
