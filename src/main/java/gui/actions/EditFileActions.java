package gui.actions;

import editor.HexEditor;
import gui.dialog.windows.InputDialogWindow;
import gui.tables.HexTable;
import gui.tables.HexTableModel;
import gui.window.MainWindow;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import java.awt.Adjustable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

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
    public static EditFileAction zeroAct;

    /**
     * The main application window.
     */
    private static MainWindow frame;

    /**
     * The table in which file data is displayed.
     */
    private static HexTable hexTable;

    /**
     * The table model of the HexTable.
     */
    private static HexTableModel tableModel;

    /**
     * The maximum size of the buffer.
     */
    private static final int maxBufferSize = 1024 * 1024 * 1024;

    /**
     * The buffer in which bytes are saved after cut and copy
     * operations.
     */
    private static byte[] byteBuffer;

    /**
     * The file to edit.
     */
    private static HexEditor hexEditor;

    /**
     * The byte offset in the file which corresponds to the anchor
     * selected cell.
     */
    private static int offset = 0;

    /**
     * Selected cell count.
     */
    private static int count;

    static {
        makeActions();
    }

    /**
     * Creates static instances for using them by buttons.
     */
    public static void makeActions() {
        cutAct = new EditFileAction(
                "Cut",
                KeyEvent.VK_X,
                KeyEvent.VK_X,
                "Cut (Alt+X)");
        copyAct = new EditFileAction(
                "Copy",
                KeyEvent.VK_C,
                KeyEvent.VK_C,
                "Copy (Alt+C)");
        pasteAct = new EditFileAction(
                "Paste",
                KeyEvent.VK_V,
                KeyEvent.VK_V,
                "Paste (Alt+V)");
        insertAct = new EditFileAction(
                "Insert",
                KeyEvent.VK_I,
                KeyEvent.VK_I,
                "Insert new bytes with replacement.");
        addAct = new EditFileAction(
                "Add",
                KeyEvent.VK_A,
                KeyEvent.VK_A,
                "Insert new bytes without replacement.");
        findAct = new EditFileAction(
                "Find",
                KeyEvent.VK_F,
                KeyEvent.VK_F,
                "Find a pattern.");
        zeroAct = new EditFileAction(
                "Zero",
                KeyEvent.VK_Z,
                KeyEvent.VK_Z,
                "Replace the selected with zeros.");

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
     * Initializes the static variables.
     *
     * @param hexTable   the table with file data
     * @param hex        the file to edit
     * @param win        the main application window
     */
    public static void init(HexTable hexTable, HexEditor hex, MainWindow win) {
        EditFileActions.hexTable = hexTable;
        hexTable.addKeyListener(new CellInput());
        EditFileActions.tableModel = hexTable.getModel();
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
        zeroAct.setEnabled(newValue);
    }

    /**
     * Updates the offset and count of the selected bytes in the
     * table.
     */
    private static void updateSelection() {
        int start = hexTable.getStartOffset();
        int end = hexTable.getEndOffset();

        offset = Math.min(start, end);
        end = Math.max(start, end);

        count = end - offset + 1;
    }

    /**
     * Copies the selected byte block to the buffer.
     */
    private static boolean copy() {
        if (count >= maxBufferSize) {
            return false;
        }
        byteBuffer = hexEditor.read(offset, count);
        return byteBuffer != null;
    }

    /**
     * Cuts the selected byte block to the buffer.
     */
    private static void cut() {
        if (copy()) {
            hexEditor.delete(offset, count);
            tableModel.updateModel();
        }
    }

    /**
     * Pastes the byte block from the buffer starting from the
     * selected cell with replacement.
     */
    private static void paste() {
        if (byteBuffer == null || byteBuffer.length == 0) {
            return;
        }
        hexEditor.add(offset, byteBuffer);
        tableModel.updateModel();
    }

    /**
     * Opens the dialog window for entering bytes inserted into the
     * selected position with the replacement.
     */
    private static void insert() {
        InputDialogWindow win = new InputDialogWindow(frame, "Insert");
        byte[] bytes = win.getData();

        if (bytes == null || bytes.length == 0) {
            return;
        }
        hexEditor.insert(offset, bytes);
        tableModel.updateModel();
    }

    /**
     * Opens the dialog window for entering bytes inserted into the
     * selected position with the rest offset to the right.
     */
    private static void add() {
        InputDialogWindow win = new InputDialogWindow(frame, "Add");
        byte[] bytes = win.getData();

        if (bytes == null || bytes.length == 0) {
            return;
        }
        hexEditor.add(offset, bytes);
        tableModel.updateModel();
    }

    /**
     * Replaces selection with zeros.
     */
    private static void resetToZero() {
        hexEditor.insertZeros(count, offset);
        tableModel.updateModel();
    }

    /**
     * Opens the dialog window for entering bytes to search in the file.
     * Highlights the cell from which the match begins or displays the
     * message that the specified sequence has not been found.
     */
    private static void find() {
        InputDialogWindow win = new InputDialogWindow(frame, "Find");
        byte[] bytes = win.getData();

        if (bytes == null || bytes.length == 0)
            return;

        long res = hexEditor.find(offset, bytes);

        if (res == -1) {
            JOptionPane.showMessageDialog(
                    frame, "A pattern was not found.");
            return;
        }

        int col = (int) (res % (tableModel.getColumnCount() - 1)) + 1;
        int row = (int) (res / (tableModel.getColumnCount() - 1));

        highlightCell(row, col);
        scrollToCell(row, col);

        frame.updateFrame();
    }

    /**
     * Selects the specified cell.
     *
     * @param row row index
     * @param col column index
     */
    private static void highlightCell(int row, int col) {
        // Select the found cell
        hexTable.getSelectionModel().addSelectionInterval(row, row);
        hexTable.getColumnModel()
                .getSelectionModel().setSelectionInterval(col, col);
    }

    /**
     * Scrolls window to specified cell.
     *
     * @param row row index
     * @param col column index
     */
    private static void scrollToCell(int row, int col) {
        // Move vertical scroll bar to the match cell
        Adjustable e = frame.fileViewPanel.getVerticalScrollBar();
        // One line corresponds to the 40 value of the scrollbar
        e.setValue(row * 40);

        // Move horizontal scroll bar to the match cell
        e = frame.fileViewPanel.getHorizontalScrollBar();
        e.setValue(col * 40);
    }

    /**
     * The class describes Action performing file edit operations.
     */
    public static class EditFileAction extends AbstractAction {
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
            updateSelection();

            // If user hasn't selected any cells
            if (offset < 0 || count < 0) {
                if (comStr.equals("Find")) {
                    // Sets the find position from the file beginning
                    offset = 0;
                }
                else {
                    return;
                }
            }

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
                case "Zero":
                    resetToZero();
                    break;
            }
        }
    }

    /**
     * The class that provides filling in the table cell using a
     * keyboard.
     */
    public static class CellInput extends KeyAdapter {

        /**
         * The number that is written to cell.
         */
        private final StringBuilder num = new StringBuilder();

        /**
         * The byte offset which corresponds to the anchor selected cell.
         */
        private int offset = -1;

        public CellInput() {
            hexTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    num.delete(0, num.length());
                    offset = hexTable.getEndOffset();
                }
            });
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (offset == -1)
                return;

            if  ((KeyEvent.VK_A <= keyCode && keyCode <= KeyEvent.VK_F)
                    || (KeyEvent.VK_0 <= keyCode && keyCode <= KeyEvent.VK_9)
                    || (KeyEvent.VK_NUMPAD0 <= keyCode
                    && keyCode <= KeyEvent.VK_NUMPAD9)) {

                byte b = getNum(e.getKeyChar());
                hexEditor.insert(offset, b);
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
            if (num.length() > 2) {
                num.delete(0, 1);
            }

            num.append(c);
            return (byte) Long.parseLong(num.toString(), 16);
        }
    }
}
