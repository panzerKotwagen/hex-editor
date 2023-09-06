package gui.window;

import gui.actions.EditFileActions;
import gui.actions.StandardFileActions;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * The application menu.
 */
public class Menu extends JMenuBar {
    Menu() {
        makeFileMenu();
        makeEditMenu();
        makeHelpMenu();
    }

    /**
     * Makes submenu File of the menu bar. Each menu item is
     * associated with appropriate FileAction.
     */
    private void makeFileMenu() {
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem mItemOpen = new JMenuItem(StandardFileActions.openAct);
        JMenuItem mItemSave = new JMenuItem(StandardFileActions.saveAct);
        JMenuItem mItemSaveAs = new JMenuItem(StandardFileActions.saveAsNewAct);
        JMenuItem mItemClose = new JMenuItem(StandardFileActions.closeAct);
        JMenuItem mItemExit = new JMenuItem(StandardFileActions.exitAct);

        menuFile.add(mItemOpen);
        menuFile.addSeparator();
        menuFile.add(mItemSave);
        menuFile.add(mItemSaveAs);
        menuFile.addSeparator();
        menuFile.add(mItemClose);
        menuFile.add(mItemExit);

        this.add(menuFile);
    }

    /**
     * Makes submenu Edit of the menu bar.
     */
    private void makeEditMenu() {
        JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic(KeyEvent.VK_E);

        JMenuItem mItemCopy = new JMenuItem(EditFileActions.copyAct);
        JMenuItem mItemCut = new JMenuItem(EditFileActions.cutAct);
        JMenuItem mItemPaste = new JMenuItem(EditFileActions.pasteAct);
        JMenuItem mItemInsert = new JMenuItem(EditFileActions.insertAct);
        JMenuItem mItemAdd = new JMenuItem(EditFileActions.addAct);

        menuEdit.add(mItemCopy);
        menuEdit.add(mItemCut);
        menuEdit.add(mItemPaste);
        menuEdit.addSeparator();
        menuEdit.add(mItemInsert);
        menuEdit.add(mItemAdd);

        this.add(menuEdit);
    }

    /**
     * Makes submenu Help of the menu bar.
     */
    private void makeHelpMenu() {
        JMenu menuHelp = new JMenu("Help");
        JMenuItem mItemAbout = new JMenuItem("About");
        menuHelp.add(mItemAbout);

        this.add(menuHelp);
    }
}
