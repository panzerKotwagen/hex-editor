package gui;

import editor.ByteSequence;
import editor.HexEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * The class that provides GUI.
 */
public class MainWindow {

    /**
     * Thew main frame.
     */
    private final JFrame frame;

    /**
     * The menu-bar of the main frame.
     */
    private final JMenuBar menuBar;

    /**
     * The toolbar of the main frame.
     */
    private JToolBar toolBar;

    /**
     * The panel for manipulation with file data.
     */
    private final JScrollPane viewFilePanel;

    /**
     * The panel on which byte decode is placed.
     */
    private final JPanel decodePanel;

    /**
     * The table in which file data is displayed.
     */
    private FileTable fileTable;

    /**
     * The class for manipulation with a file.
     */
    private final HexEditor hexEditor;

    /**
     * A variable indicating whether the file is open or not.
     */
    private boolean fileIsOpened = false;

    private final HashMap<String, JTextField> textFields = new HashMap<>();

    private final String[] labelTexts = {
            "Signed 8 bit", "Signed 32 bit", "Unsigned 8 bit",
            "Unsigned 32 bit", "Signed 16 bit", "Signed 64 bit",
            "Unsigned 16 bit", "Unsigned 64 bit", "Float 32 bit",
            "Double 64 bit"};

    private FileAction openAct;
    private FileAction saveAct;
    private FileAction closeAct;
    private FileAction saveAsNewAct;
    private FileAction exitAct;

    /**
     * Initialize the application window.
     */
    MainWindow() {
        setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 20));

        frame = new JFrame("Hex editor");
        frame.setMinimumSize(new Dimension(600, 600));

        menuBar = new JMenuBar();

        makeFileActions();

        makeFileMenu();
        makeEditMenu();
        makeHelpMenu();

        makeToolBar();

        decodePanel = new JPanel(new GridLayout(5, 4, 5, 5));

        viewFilePanel = new JScrollPane();

        makeBitValuesPanel();

        frame.add(toolBar, BorderLayout.NORTH);

        frame.setJMenuBar(menuBar);

        decodePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        frame.add(decodePanel, BorderLayout.SOUTH);

        frame.add(viewFilePanel, BorderLayout.CENTER);

        hexEditor = new HexEditor();

        // Monitors the window resizing events for the table redrawing
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (fileIsOpened) {
                    fileTable.updateTableView(frame.getBounds().width);
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        frame.setVisible(true);
    }

    /**
     * Sets the specified font for every component.
     *
     * @param f new font
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    /**
     * Creates the FileAction objects.
     */
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
        unblockFileButtons(false);
    }

    /**
     * Makes submenu File of the menu bar. Each menu item is
     * associated with appropriate FileAction.
     */
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

    /**
     * Makes submenu Edit of the menu bar.
     */
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

    /**
     * Makes submenu Help of the menu bar.
     */
    private void makeHelpMenu() {
        JMenu menuHelp = new JMenu("Help");
        JMenuItem mItemAbout = new JMenuItem("About");
        menuHelp.add(mItemAbout);

        menuBar.add(menuHelp);
    }

    /**
     * Makes the toolbar. Each button is associated with appropriate
     * Action.
     */
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

    /**
     * Makes the panel on which bit represent values are placed on.
     */
    private void makeBitValuesPanel() {
        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel(labelTexts[i]);
            label.setHorizontalAlignment(JLabel.RIGHT);
            JTextField textField = new JTextField();
            textField.setEnabled(false);
            textField.setDisabledTextColor(Color.BLACK);
            textFields.put(labelTexts[i], textField);
            decodePanel.add(label);
            decodePanel.add(textField);
        }
    }

    /**
     * Updates the application window to display the changed elements.
     */
    private void updateFrame() {
        SwingUtilities.updateComponentTreeUI(frame);
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

        unblockFileButtons(true);

        fileIsOpened = true;

        createTable();
    }

    /**
     * Sets whether the Save and Close actions are enabled.
     *
     * @param newValue true to enable, false to disable
     */
    private void unblockFileButtons(boolean newValue) {
        saveAct.setEnabled(newValue);
        saveAsNewAct.setEnabled(newValue);
        closeAct.setEnabled(newValue);
    }

    /**
     * Creates and display the table with the opened file data.
     */
    private void createTable() {
        FileTableModel tableModel = new FileTableModel(16);

        tableModel.setDataSource(hexEditor);

        fileTable = new FileTable(tableModel);

        fileTable.updateTableView(frame.getBounds().width);
        
        fileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileTable.updateSelectedCellIndex();
                fillBitPanel();
            }
        });

        viewFilePanel.setViewportView(fileTable);

        updateFrame();
    }

    /**
     * Fills the bit represent panel with the values of byte block.
     * The byte block is a sequence of bytes starting from the
     * selected byte and 7 more to the right of it.
     */
    private void fillBitPanel() {
        FileTableModel model = (FileTableModel) fileTable.getModel();
        byte[] array = new byte[8];

        for (int i = 0; i < 8; i++) {
            try {

                array[i] = model.getValueByIndex(model.getIndex(
                        fileTable.selectedRowIndexStart,
                        fileTable.selectedColIndexStart));
            }
            // If the number of bytes in the file starting from the
            // selected position is less than 8
            catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        ByteSequence byteSequence = new ByteSequence(array);

        textFields.get("Signed 8 bit").setText(String.valueOf(
                byteSequence.representAsSigned8Bit(0)));
        textFields.get("Unsigned 8 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned8Bit(0)));
        textFields.get("Signed 16 bit").setText(String.valueOf(
                byteSequence.representAsSigned16Bit(0)));
        textFields.get("Unsigned 16 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned16Bit(0)));
        textFields.get("Signed 32 bit").setText(String.valueOf(
                byteSequence.representAsSigned32Bit(0)));
        textFields.get("Unsigned 32 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned32Bit(0)));
        textFields.get("Signed 64 bit").setText(String.valueOf(
                byteSequence.representAsSigned64Bit(0)));
        textFields.get("Unsigned 64 bit").setText(String.valueOf(
                byteSequence.representAsUnsigned64Bit(0)));
        textFields.get("Float 32 bit").setText(String.valueOf(
                byteSequence.representAsFloat(0)));
        textFields.get("Double 64 bit").setText(String.valueOf(
                byteSequence.representAsDouble(0)));
    }

    /**
     * Closes the current opened file.
     */
    private void closeFile() {
        if (!fileIsOpened)
            return;

        if (!hexEditor.closeFile()) {
            System.err.println("Error: failed to close file");
            return;
        }

        viewFilePanel.getViewport().remove(0);

        unblockFileButtons(false);

        updateFrame();
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
        if (fileIsOpened)
            closeFile();
        System.exit(0);
    }

    /**
     * The Action class for the default file operations: Open,
     * Close, Save, Exit.
     */
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
}
