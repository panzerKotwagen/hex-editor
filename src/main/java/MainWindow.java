import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * The class that provides GUI.
 */
public class MainWindow {

    /**
     * Thew main frame.
     */
    private JFrame frame;

    /**
     * The menu-bar of the main frame.
     */
    private JMenuBar menuBar;

    /**
     * The toolbar of the main frame.
     */
    private JToolBar toolBar;

    /**
     * The panel for manipulation with file data.
     */
    private JScrollPane viewFilePanel;

    /**
     * The panel on which byte decode is placed.
     */
    private JPanel decodePanel;

    /**
     * The table model in which file data is stored.
     */
    private DefaultTableModel tableModel;

    /**
     * The class for manipulation with a file.
     */
    private HexEditor hexEditor;

    private FileAction openAct;
    private FileAction saveAct;
    private FileAction closeAct;
    private FileAction saveAsNewAct;
    private FileAction exitAct;

    MainWindow() {
        frame = new JFrame("Menu");

        frame.setSize(700, 600);
        frame.setMinimumSize(new Dimension(600, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();

        makeFileActions();

        makeFileMenu();
        makeEditMenu();
        makeHelpMenu();

        makeToolBar();

        tableModel = new DefaultTableModel();

        decodePanel = new JPanel(new GridLayout(5, 4, 5, 5));

        viewFilePanel = new JScrollPane();

        makeBitValuesPanel();

        frame.add(toolBar, BorderLayout.NORTH);

        frame.setJMenuBar(menuBar);

        frame.add(decodePanel, BorderLayout.SOUTH);

//        frame.add(viewFilePanel, BorderLayout.CENTER);

        hexEditor = new HexEditor();

        frame.setVisible(true);
    }

    private class FileAction extends AbstractAction {
        public FileAction(String name, int mnem,
                          int accel, String tTip) {
            super(name);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel,
                    InputEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, mnem);
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

    private void makeFileActions() {
        openAct = new FileAction(
                "Open",
                KeyEvent.VK_O,
                KeyEvent.VK_O,
                "Creates a file dialog window for loading a file.");
        saveAct = new FileAction(
                "Save",
                KeyEvent.VK_S,
                KeyEvent.VK_S,
                "Save the current opened file with replacement.");
        saveAsNewAct = new FileAction(
                "Save As",
                KeyEvent.VK_S,
                KeyEvent.VK_S,
                "Creates a file dialog window for saving a new file.");
        exitAct = new FileAction(
                "Exit",
                KeyEvent.VK_Q,
                KeyEvent.VK_Q,
                "Close the editor.");
        closeAct = new FileAction(
                "Close",
                KeyEvent.VK_W,
                KeyEvent.VK_W,
                "Close the current opened file.");

        saveAsNewAct.putValue(FileAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.SHIFT_MASK));

        // The functions are not available until a file is opened
        saveAct.setEnabled(false);
        saveAsNewAct.setEnabled(false);
        closeAct.setEnabled(false);
    }

    private void makeFileMenu() {
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem mItemOpen = new JMenuItem(openAct);
        JMenuItem mItemSave = new JMenuItem(saveAct);
        JMenuItem mItemSaveAs = new JMenuItem(saveAsNewAct);
        JMenuItem mItemClose = new JMenuItem(closeAct);
        JMenuItem mItemExit = new JMenuItem(exitAct);

        menuFile.add(mItemOpen);
        menuFile.addSeparator();
        menuFile.add(mItemSave);
        menuFile.add(mItemSaveAs);
        menuFile.addSeparator();
        menuFile.add(mItemClose);
        menuFile.add(mItemExit);

        menuBar.add(menuFile);
    }

    private void makeEditMenu() {
        JMenu menuEdit = new JMenu("Edit");

        JMenuItem mItemCopy = new JMenuItem("Copy");
        JMenuItem mItemCut = new JMenuItem("Cut");
        JMenuItem mItemPaste = new JMenuItem("Paste");
        JMenuItem mItemFind = new JMenuItem("Find");

        mItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        mItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        mItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        mItemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));

        menuEdit.add(mItemCopy);
        menuEdit.add(mItemCut);
        menuEdit.add(mItemPaste);
        menuEdit.addSeparator();
        menuEdit.add(mItemFind);

        menuBar.add(menuEdit);
    }

    private void makeHelpMenu() {
        JMenu menuHelp = new JMenu("Help");
        JMenuItem mItemAbout = new JMenuItem("About");
        menuHelp.add(mItemAbout);

        menuBar.add(menuHelp);
    }

    private void makeToolBar() {
        toolBar = new JToolBar("Tools");
        toolBar.setFloatable(false);

        JButton btnOpen = new JButton(openAct);
        JButton btnClose = new JButton(closeAct);
        JButton btnSave = new JButton(saveAct);
        JButton btnSaveAs = new JButton(saveAsNewAct);

        toolBar.add(btnOpen);
        toolBar.add(btnClose);
        toolBar.add(btnSave);
        toolBar.add(btnSaveAs);
    }

    private void makeBitValuesPanel() {

        String[] bits = {"8", "32", "8", "32", "16", "64", "16", "64", "32", "64"};
        String[] sign = {"Signed", "Unsigned"};

        for (int i = 0; i < 8; i++) {
            JLabel label = new JLabel(sign[i / 2 % 2] + " " + bits[i % bits.length] + " bit");
            label.setHorizontalAlignment(JLabel.RIGHT);
            JTextField textField = new JTextField();
            textField.setEnabled(false);
            decodePanel.add(label);
            decodePanel.add(textField);
        }

        JLabel label = new JLabel("Float 32 bit");
        label.setHorizontalAlignment(JLabel.RIGHT);
        JTextField textField = new JTextField();
        textField.setEnabled(false);
        decodePanel.add(label);
        decodePanel.add(textField);

        label = new JLabel("Double 64 bit");
        label.setHorizontalAlignment(JLabel.RIGHT);
        textField = new JTextField();
        textField.setEnabled(false);
        decodePanel.add(label);
        decodePanel.add(textField);
    }

    /**
     * Launches the file manager window to open an existing file.
     */
    private void openFile() {
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

        saveAct.setEnabled(true);
        saveAsNewAct.setEnabled(true);
        closeAct.setEnabled(true);
    }

    /**
     * Close the current opened file.
     */
    private void closeFile() {
        if (!hexEditor.closeFile()) {
            System.err.println("Error: failed to close file");
            return;
        }

        saveAct.setEnabled(false);
        saveAsNewAct.setEnabled(false);
        closeAct.setEnabled(false);
    }

    /**
     * Saves the current opened file.
     */
    private void saveFile() {
        if (!hexEditor.saveFile()) {
            System.err.println("Error: failed to save file");
        }
    }

    /**
     * Opens the file manager window to save the file as new one.
     */
    private void saveAsNewFile() {
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
     * Close the program.
     */
    private void exit() {
        closeFile();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
