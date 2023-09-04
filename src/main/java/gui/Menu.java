package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;

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

        JMenuItem mItemOpen = new JMenuItem(MainWindow.openAct);
        JMenuItem mItemSave = new JMenuItem(MainWindow.saveAct);
        JMenuItem mItemSaveAs = new JMenuItem(MainWindow.saveAsNewAct);
        JMenuItem mItemClose = new JMenuItem(MainWindow.closeAct);
        JMenuItem mItemExit = new JMenuItem(MainWindow.exitAct);

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

        JMenuItem mItemCopy = new JMenuItem(MainWindow.copyAct);
        JMenuItem mItemCut = new JMenuItem(MainWindow.cutAct);
        JMenuItem mItemPaste = new JMenuItem(MainWindow.pasteAct);

        menuEdit.add(mItemCopy);
        menuEdit.add(mItemCut);
        menuEdit.add(mItemPaste);
        menuEdit.addSeparator();

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
