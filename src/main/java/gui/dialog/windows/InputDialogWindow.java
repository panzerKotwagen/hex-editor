package gui.dialog.windows;

import gui.tables.InputTable;
import gui.window.MainWindow;

import javax.swing.*;
import java.awt.*;

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
     * The label placed
     */
    private JLabel label;

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
     * Constructs the dialog window.
     */
    public InputDialogWindow(JFrame owner, String name) {
        super(owner, name, true);

        MainWindow.setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 18));

        this.setMinimumSize(new Dimension(420, 350));

        this.setLocationRelativeTo(null);

        this.setResizable(false);

        InputTable table = new InputTable();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        panelLabel = new JPanel(new FlowLayout());

        panelLabel.add(label = new JLabel("Input the bytes"));

        panelTable = new JScrollPane(table);

        panelButtons = new JPanel(new FlowLayout());

        panelButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelTable.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnDo = new JButton("Do");
        btnCancel = new JButton("Cancel");

        btnDo.addActionListener(e -> {
            data = table.getData();
            this.setVisible(false);
        });

        btnCancel.addActionListener(e -> this.setVisible(false));

        panelButtons.add(btnDo);
        panelButtons.add(btnCancel);

        this.add(panelLabel, BorderLayout.NORTH);
        this.add(panelTable, BorderLayout.CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);

        this.show();
        this.dispose();
    }

    /**
     * Returns the block of bytes entered by the user.
     * @return byte array
     */
    public byte[] getData() {
        return data;
    }
}
