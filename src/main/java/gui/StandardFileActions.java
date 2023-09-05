package gui;

import editor.HexEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//TODO: Class description
public class StandardFileActions {
    public static StandardFileAction openAct;
    public static StandardFileAction saveAct;
    public static StandardFileAction closeAct;
    public static StandardFileAction saveAsNewAct;
    public static StandardFileAction exitAct;

    /**
     * The var for manipulation with a file.
     */
    private static HexEditor hexEditor;

    /**
     * A variable indicating whether the file is open or not.
     */
    private static boolean fileIsOpened;

    /**
     * The main application window.
     */
    private static MainWindow frame;

    public StandardFileActions(MainWindow frame) {
        StandardFileActions.frame = frame;
        StandardFileActions.hexEditor = new HexEditor();
        makeActions();
    }

    public class StandardFileAction extends AbstractAction {
        public StandardFileAction(String name, int mnemonicKey,
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
                case "Open":
                    openFile();
                    break;
                case "Close":
                    closeFile();
                    break;
                case "Save":
                    saveFile();
                    break;
                case "Save As":
                    saveAsNewFile();
                    break;
                case "Exit":
                    exit();
                    break;
            }
        }
    }

    /**
     * Creates the FileAction objects.
     */
    private void makeActions() {
        openAct = new StandardFileAction(
                "Open",
                KeyEvent.VK_O,
                KeyEvent.VK_O,
                "Creates a file dialog window for loading a file.");
        saveAct = new StandardFileAction(
                "Save",
                KeyEvent.VK_S,
                KeyEvent.VK_S,
                "Save the current opened file with replacement.");
        saveAsNewAct = new StandardFileAction(
                "Save As",
                KeyEvent.VK_S,
                KeyEvent.VK_S,
                "Creates a file dialog window for saving a new file.");
        exitAct = new StandardFileAction(
                "Exit",
                KeyEvent.VK_Q,
                KeyEvent.VK_Q,
                "Close the editor.");
        closeAct = new StandardFileAction(
                "Close",
                KeyEvent.VK_W,
                KeyEvent.VK_W,
                "Close the current opened file.");

        saveAsNewAct.putValue(StandardFileAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.SHIFT_DOWN_MASK));

        // The functions are not available until a file is opened
        unblockFileButtons(false);
    }

    //TODO: Rewrite
    /**
     * Sets whether the Save and Close actions are enabled.
     *
     * @param newValue true to enable, false to disable
     */
    private void unblockFileButtons(boolean newValue) {
        saveAct.setEnabled(newValue);
        saveAsNewAct.setEnabled(newValue);
        closeAct.setEnabled(newValue);
        EditFileActions.unblockEditActions(newValue);
    }

    /**
     * Shows the dialog window in which the user is prompted to save
     * the file.
     *
     * @return user response
     */
    private int showConfirmSavingDialog() {
        int response = JOptionPane.showConfirmDialog(
                frame, "Do you want to save changes?");

        if (response == JOptionPane.YES_OPTION) {
            saveFile();
        }

        return response;
    }

    /**
     * Launches the file manager window to open an existing file.
     */
    private void openFile() {
        if (fileIsOpened) {
            int response = showConfirmSavingDialog();

            if (response == JOptionPane.CANCEL_OPTION)
                return;
        }

        FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setVisible(true);

        String dir = fd.getDirectory();
        String filename = fd.getFile();

        if (filename == null)
            return;

        if (!hexEditor.openFile(dir + filename)) {
            System.err.println("Error: failed to open file");
            return;
        }

        unblockFileButtons(true);

        fileIsOpened = true;

        FileTable table = FileTable.createTable(hexEditor);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                table.updateSelectedCellIndex();
                frame.decodePanel.fillPane(table.getByteSequence());
            }
        });

        EditFileActions.init(table, (FileTableModel) table.getModel(), hexEditor);

        // Monitors the window resizing events for the table redrawing
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (fileIsOpened) {
                    table.updateTableView(frame.getBounds().width);
                }
            }
        });

        frame.viewFilePane.setViewportView(table);

        frame.updateFrame();
    }

    /**
     * Closes the current opened file.
     */
    private void closeFile() {
        if (!fileIsOpened)
            return;

        int response = showConfirmSavingDialog();

        if (response == JOptionPane.CANCEL_OPTION)
            return;

        if (!hexEditor.closeFile()) {
            System.err.println("Error: failed to close file");
            return;
        }

        frame.viewFilePane.getViewport().remove(0);

        unblockFileButtons(false);

        fileIsOpened = false;

        frame.updateFrame();
    }

    /**
     * Saves the current opened file.
     */
    private void saveFile() {
        if (!fileIsOpened)
            return;

        if (!hexEditor.saveFile()) {
            System.err.println("Error: failed to save file");
        }
    }

    /**
     * Opens the file manager window to save the file as new one.
     */
    private void saveAsNewFile() {
        if (!fileIsOpened)
            return;

        FileDialog fd = new FileDialog(frame, "Save the file", FileDialog.SAVE);
        fd.setVisible(true);

        String dir = fd.getDirectory();
        String filename = fd.getFile();

        if (filename == null)
            return;

        if (!hexEditor.saveAsNewFile(dir + filename)) {
            System.err.println("Error: failed to save file");
        }
    }

    /**
     * Closes the program.
     */
    private void exit() {
        if (!fileIsOpened) {
            System.exit(0);
        }

        int response = showConfirmSavingDialog();

        if (response == JOptionPane.CANCEL_OPTION)
            return;

        hexEditor.closeFile();
        System.exit(0);
    }
}
