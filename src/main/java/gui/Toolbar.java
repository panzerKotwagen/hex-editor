package gui;

import javax.swing.*;

public class Toolbar extends JToolBar {
    /**
     * Makes the toolbar. Each button is associated with appropriate
     * Action.
     */
    public Toolbar() {
        super("Tools");
        this.setFloatable(false);

        JButton btnOpen = new JButton(MainWindow.openAct);
        JButton btnClose = new JButton(MainWindow.closeAct);
        JButton btnSave = new JButton(MainWindow.saveAct);
        JButton btnSaveAs = new JButton(MainWindow.saveAsNewAct);
        JButton btnCut = new JButton(MainWindow.cutAct);
        JButton btnCopy = new JButton(MainWindow.copyAct);
        JButton btnPaste = new JButton(MainWindow.pasteAct);

        this.add(btnOpen);
        this.add(btnClose);
        this.add(btnSave);
        this.add(btnSaveAs);
        this.add(btnCut);
        this.add(btnCopy);
        this.add(btnPaste);
    }
}
