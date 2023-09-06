package gui.dialog.windows;

import gui.tables.InputTable;
import gui.window.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 * The class describes dialog window in which a user can enter a
 * block of bytes.
 */
public class InputDialogWindow extends JDialog {
    private byte[] data;
    private JLabel label;
    private JButton btnDo;
    private JButton btnCancel;
    private JPanel panelButtons;
    private JPanel panelLabel;
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

//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        btnCancel.addActionListener(e -> {
            data = table.getData();
            this.setVisible(false);
        });

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
