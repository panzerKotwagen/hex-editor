import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * The class that provides GUI.
 */
public class MainWindow {
    JMenuBar menuBar;

    JToolBar toolBar;

    MainWindow() {
        JFrame frame = new JFrame("Menu");

        frame.setSize(700, 600);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();

        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
