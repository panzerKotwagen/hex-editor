package gui;

import editor.HexEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

//TODO: Class description
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
    private int offset;

    /**
     * Selected cell count.
     */
    private int count;

    public EditFileActions() {
        makeEditFileActions();
    }

    public static void init(FileTable fileTable, FileTableModel tableModel, HexEditor hex) {
        EditFileActions.fileTable = fileTable;
        EditFileActions.tableModel = tableModel;
        EditFileActions.hexEditor = hex;
    }

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
                    insert();
                    break;
            }
        }
    }

    private void updateSelectedByteIndex() {
        offset = tableModel.getIndex(fileTable.selectedRowIndexStart, fileTable.selectedColIndexStart);
        int end = tableModel.getIndex(fileTable.selectedRowIndexEnd, fileTable.selectedColIndexEnd);
        count = end - offset + 1;
    }

    public static void unblockEditActions(boolean newValue) {
        copyAct.setEnabled(newValue);
        cutAct.setEnabled(newValue);
        pasteAct.setEnabled(newValue);
    }

    /**
     * Cuts the selected byte block into clipboard.
     */
    private void cut() {
        updateSelectedByteIndex();
        copy();
        hexEditor.delete(offset, count);
        tableModel.updateTable();
    }

    /**
     * Copies the selected byte block into clipboard.
     */
    private void copy() {
        updateSelectedByteIndex();
        byteClipboard = new byte[count];
        for (int i = 0; i < count; i++) {
            byteClipboard[i] = tableModel.getValueByIndex(offset + i);
        }
    }

    /**
     * Pastes the byte block from the clipboard starting from the
     * selected cell with replacement.
     */
    private void insert() {
        updateSelectedByteIndex();
        hexEditor.insert(offset, byteClipboard);
        tableModel.updateTable();
    }

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
}
