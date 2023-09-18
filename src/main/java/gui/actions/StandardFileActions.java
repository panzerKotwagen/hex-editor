package gui.actions;

import editor.HexEditor;
import gui.tables.HexTable;
import gui.tables.HexTableModel;
import gui.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The class that provides file operations: open, close, save, exit
 * and static variables of the StandardFileAction objects
 * for using them by menu and toolbar buttons.
 */
public class StandardFileActions {
    public static StandardFileAction openAct;
    public static StandardFileAction saveAct;
    public static StandardFileAction closeAct;
    public static StandardFileAction saveAsNewAct;
    public static StandardFileAction exitAct;

    /**
     * The variable for manipulation with a file.
     */
    private static HexEditor hexEditor;

    /**
     * The variable indicating whether the file is open or not.
     */
    private static boolean fileIsOpened = false;

    /**
     * The main application window.
     */
    private static MainWindow frame;

    /**
     * Initializes the static variables and constructs the actions.
     *
     * @param frame main application window
     */
    public StandardFileActions(MainWindow frame) {
        StandardFileActions.frame = frame;
        StandardFileActions.hexEditor = new HexEditor();
        makeActions();
    }

    /**
     * The class describes Action performing the following file
     * operations: open, close, save, save as, exit.
     */
    public static class StandardFileAction extends AbstractAction {
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
                    open();
                    break;
                case "Close":
                    close();
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
     * Constructs the StandardFileAction objects.
     */
    private static void makeActions() {
        openAct = new StandardFileAction(
                "Open",
                KeyEvent.VK_O,
                KeyEvent.VK_O,
                "Open (Ctrl+O)");
        saveAct = new StandardFileAction(
                "Save",
                KeyEvent.VK_S,
                KeyEvent.VK_S,
                "Save (Ctrl+S)");
        saveAsNewAct = new StandardFileAction(
                "Save As",
                KeyEvent.VK_S,
                KeyEvent.VK_S,
                "Save As (Ctrl+Shift+S)");
        exitAct = new StandardFileAction(
                "Exit",
                KeyEvent.VK_Q,
                KeyEvent.VK_Q,
                "Exit (Ctrl+Q)");
        closeAct = new StandardFileAction(
                "Close",
                KeyEvent.VK_W,
                KeyEvent.VK_W,
                "Close (Ctrl+W)");

        saveAsNewAct.putValue(
                StandardFileAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S,
                        InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK));

        // The actions are not available until a file is opened
        unblockFileButtons();
    }

    /**
     * Sets whether actions are enabled depending on whether a file
     * is open.
     */
    private static void unblockFileButtons() {
        saveAct.setEnabled(fileIsOpened);
        saveAsNewAct.setEnabled(fileIsOpened);
        closeAct.setEnabled(fileIsOpened);
        EditFileActions.unblockEditActions(fileIsOpened);
    }

    /**
     * Shows the dialog window in which the user is prompted to save
     * the file.
     *
     * @return false if the cancel was pressed, true otherwise
     */
    private static boolean maybeSave() {
        if (!fileIsOpened)
            return true;

        int response = JOptionPane.showConfirmDialog(
                frame, "Do you want to save changes?");

        if (response == JOptionPane.YES_OPTION) {
            return saveFile();
        } else return response != JOptionPane.CANCEL_OPTION;
    }

    /**
     * Launches the file manager window to open an existing file. If
     * a file was not chosen returns null otherwise returns path to
     * the file in String format.
     *
     * @return path to the file or null
     */
    private static String maybeOpen() {
        FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setVisible(true);

        String dir = fd.getDirectory();
        String filename = fd.getFile();

        if (filename == null)
            return null;
        else
            return dir + filename;
    }

    /**
     * Launches the file manager window to open an existing file.
     * If the file has been selected creates the table and displays
     * it on the screen.
     */
    private static void open() {
        if (!maybeSave()) {
            return;
        }

        String path = maybeOpen();

        if (path == null)
            return;

        if (fileIsOpened)
            hexEditor.closeFile();

        hexEditor.openFile(path);

        fileIsOpened = true;
        unblockFileButtons();

        createTable();

        frame.updateFrame();
    }

    /**
     * Makes the table and all the listeners for it.
     */
    private static void createTable() {
        HexTable table = HexTable.createTable(hexEditor);

        table.updateTableView(frame.getBounds().width);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                frame.decodePanel.fillPane(table.getByteSequence());
            }
        });

        EditFileActions.init(
                table,
                (HexTableModel) table.getModel(),
                hexEditor,
                frame);

        // Monitors the window resizing events for the table redrawing
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (fileIsOpened) {
                    table.updateTableView(frame.getBounds().width);
                }
            }
        });

        frame.fileViewPanel.setViewportView(table);
    }

    /**
     * Closes the current opened file.
     */
    private static void close() {
        if (maybeSave()) {
            hexEditor.closeFile();
            frame.fileViewPanel.getViewport().remove(0);
            fileIsOpened = false;
            unblockFileButtons();
            frame.updateFrame();
        }
    }

    /**
     * Saves the current opened file.
     */
    private static boolean saveFile() {
        if (!fileIsOpened) {
            return false;
        } else {
            return hexEditor.saveFile();
        }
    }

    /**
     * Opens the file manager window to save the file as new one.
     */
    private static boolean saveAsNewFile() {
        if (!fileIsOpened)
            return false;

        FileDialog fd = new FileDialog(frame, "Save the file", FileDialog.SAVE);
        fd.setVisible(true);

        String dir = fd.getDirectory();
        String filename = fd.getFile();

        if (filename == null)
            return false;

        return hexEditor.saveAsNewFile(dir + filename);
    }

    /**
     * Closes the program.
     */
    private static void exit() {
        if (!maybeSave()) {
            return;
        }

        hexEditor.closeFile();
        System.exit(0);
    }
}
