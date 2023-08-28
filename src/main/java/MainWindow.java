import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * The class that provides GUI.
 */
public class MainWindow {
    JMenuBar menuBar;

    JToolBar toolBar;

    JPanel viewFilePanel;

    JPanel decodePanel;

    MainWindow() {
        JFrame frame = new JFrame("Menu");

        frame.setSize(700, 600);
        frame.setMinimumSize(new Dimension(600,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();

        makeFileMenu();
        makeEditMenu();
        makeHelpMenu();

        makeToolBar();

        frame.add(toolBar, BorderLayout.NORTH);

        frame.setJMenuBar(menuBar);

        decodePanel = new JPanel(new GridLayout(5, 4, 5, 5));

        makeBitValuesPanel();

        frame.add(decodePanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    void makeFileMenu() {
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem mItemOpen = new JMenuItem("Open", KeyEvent.VK_O);
        JMenuItem mItemSave = new JMenuItem("Save", KeyEvent.VK_S);
        JMenuItem mItemSaveAs = new JMenuItem("Save As", KeyEvent.VK_S);
        JMenuItem mItemClose = new JMenuItem("Close", KeyEvent.VK_W);
        JMenuItem mItemExit = new JMenuItem("Exit", KeyEvent.VK_Q);

        mItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK));
        mItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        mItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

        menuFile.add(mItemOpen);
        menuFile.addSeparator();
        menuFile.add(mItemSave);
        menuFile.add(mItemSaveAs);
        menuFile.addSeparator();
        menuFile.add(mItemClose);
        menuFile.add(mItemExit);

        menuBar.add(menuFile);
    }

    void makeEditMenu() {
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

    void makeHelpMenu() {
        JMenu menuHelp = new JMenu("Help");
        JMenuItem mItemAbout = new JMenuItem("About");
        menuHelp.add(mItemAbout);

        menuBar.add(menuHelp);
    }

    void makeToolBar() {
        toolBar = new JToolBar("Tools");
        toolBar.setFloatable(false);

        JButton btnOpen = new JButton("O");
        JButton btnClose = new JButton("C");
        JButton btnSave = new JButton("S");
        JButton btnSaveAs = new JButton("SA");

        toolBar.add(btnOpen);
        toolBar.add(btnClose);
        toolBar.add(btnSave);
        toolBar.add(btnSaveAs);
    }

    void makeBitValuesPanel() {


        String[] bits = {"8", "32", "8", "32", "16", "64", "16", "64", "32", "64"};
        String[] sign = {"Signed", "Unsigned"};

        for (int i = 0; i < 8; i++) {
            JLabel label = new JLabel(sign[i / 2 % 2] + " " + bits[i % bits.length] + " bit");
            label.setHorizontalAlignment(JLabel.RIGHT);
            JTextField textField = new JTextField();
            decodePanel.add(label);
            decodePanel.add(textField);
        }

        JLabel label = new JLabel("Float 32 bit");
        label.setHorizontalAlignment(JLabel.RIGHT);
        decodePanel.add(label);
        decodePanel.add(new JTextField());

        label = new JLabel("Double 64 bit");
        label.setHorizontalAlignment(JLabel.RIGHT);
        decodePanel.add(label);
        decodePanel.add(new JTextField());
    }

    void makeTable() {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
