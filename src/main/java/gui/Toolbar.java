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

        JButton btnOpen = new JButton(StandardFileActions.openAct);
        JButton btnClose = new JButton(StandardFileActions.closeAct);
        JButton btnSave = new JButton(StandardFileActions.saveAct);
        JButton btnSaveAs = new JButton(StandardFileActions.saveAsNewAct);

        JButton btnCut = new JButton(EditFileActions.cutAct);
        JButton btnCopy = new JButton(EditFileActions.copyAct);
        JButton btnPaste = new JButton(EditFileActions.pasteAct);

        this.add(btnOpen);
        this.add(btnClose);
        this.add(btnSave);
        this.add(btnSaveAs);
        this.add(btnCut);
        this.add(btnCopy);
        this.add(btnPaste);
    }
}
