package gui.dialog.windows;

import gui.tables.InputTable;
import gui.window.MainWindow;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The class describes the dialog window in which a user can enter a
 * block of bytes.
 */
public class InputDialogWindow extends JDialog {

    /**
     * The inputted data.
     */
    private byte[] data;

    /**
     * The button which performs the action.
     */
    private JButton btnDo;

    /**
     * The button which cancels action and closes the window.
     */
    private JButton btnCancel;

    /**
     * The panel with the buttons.
     */
    private JPanel panelButtons;

    /**
     * The panel for placing the label.
     */
    private JPanel panelLabel;

    /**
     * The panel in which the table is placed.
     */
    private JScrollPane panelTable;

    /**
     * The table displayed in the window.
     */
    private InputTable table;

    /**
     * Constructs the dialog window.
     */
    public InputDialogWindow(JFrame owner, String name) {
        super(owner, name, true);

        MainWindow.setUIFont(new javax.swing.plaf.FontUIResource(
                "Arial", Font.PLAIN, 18));

        this.setMinimumSize(new Dimension(420, 350));

        this.setLocationRelativeTo(null);

        this.setResizable(false);

        makeLabelPanel();
        makeTablePanel();
        makeButtonPanel();

        this.add(panelLabel, BorderLayout.NORTH);
        this.add(panelTable, BorderLayout.CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);

        this.setVisible(true);
        this.dispose();
    }

    /**
     * Returns the block of bytes entered by the user.
     *
     * @return byte array
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Creates the panel that contains the label.
     */
    private void makeLabelPanel() {
        panelLabel = new JPanel(new FlowLayout());
        panelLabel.add(new JLabel("Input the bytes"));
    }

    /**
     * Constructs the panel with the buttons.
     */
    private void makeButtonPanel() {
        panelButtons = new JPanel(new FlowLayout());
        panelButtons.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));

        btnDo = new JButton("Do");
        btnCancel = new JButton("Cancel");

        btnDo.addActionListener(e -> {
            data = table.getData();
            this.setVisible(false);
        });

        btnCancel.addActionListener(e -> this.setVisible(false));

        panelButtons.add(btnDo);
        panelButtons.add(btnCancel);
    }

    /**
     * Constructs the panel that contains the table.
     */
    private void makeTablePanel() {
        table = new InputTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        panelTable = new JScrollPane(table);
        panelTable.setBorder(BorderFactory.createEmptyBorder(
                10, 0, 0, 0));

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    btnDo.doClick();
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    btnCancel.doClick();
            }
        });
    }
}
