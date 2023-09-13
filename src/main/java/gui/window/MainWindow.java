package gui.window;

import gui.actions.EditFileActions;
import gui.actions.StandardFileActions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

/**
 * The class that provides GUI.
 */
public class MainWindow extends JFrame {

    /**
     * The pane for manipulation with file data.
     */
    public final JScrollPane fileViewPanel;

    /**
     * The pane on which byte decode is placed.
     */
    public final ByteRepresentPanel decodePanel;

    /**
     * Initializes the application window.
     */
    MainWindow() {
        super("Hex editor");
        setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 18));

        this.setMinimumSize(new Dimension(650, 600));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setLocationRelativeTo(null);

        new EditFileActions();
        new StandardFileActions(this);

        Menu menuBar = new Menu();

        Toolbar toolBar = new Toolbar();

        decodePanel = new ByteRepresentPanel();

        fileViewPanel = new JScrollPane();

        this.add(toolBar, BorderLayout.NORTH);

        this.setJMenuBar(menuBar);

        this.add(decodePanel, BorderLayout.SOUTH);

        this.add(fileViewPanel, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Do click onto Exit button
                menuBar.getMenu(0).getItem(6).doClick();
            }
        });

        this.setVisible(true);
    }

    /**
     * Sets the specified font for every component.
     *
     * @param font new font
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, font);
        }
    }

    /**
     * Updates the application window to display the changed elements.
     */
    public void updateFrame() {
        SwingUtilities.updateComponentTreeUI(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
