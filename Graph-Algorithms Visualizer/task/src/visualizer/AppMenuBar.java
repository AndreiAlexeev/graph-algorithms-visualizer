package visualizer;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class AppMenuBar extends JMenuBar {

    JMenu jModeMenu;
    JMenuItem itemVertex;
    JMenuItem itemEdge;
    JMenuItem itemNone;
    JMenuItem itemRemoveAVertex;
    JMenuItem itemRemoveAnEdge;

    JMenu jFileMenu;
    JMenuItem itemNew;
    JMenuItem itemExit;

    public AppMenuBar() {
        super();
        initMenuBar();
    }

    public void initMenuBar() {
        //File menu
        jFileMenu = new JMenu("File");
        jFileMenu.setName("File");
        jFileMenu.setMnemonic(KeyEvent.VK_F);
        jFileMenu.getAccessibleContext()
                .setAccessibleDescription("This file menu has menu items");
        this.add(jFileMenu);

        itemNew = new JMenuItem("New");
        itemNew.setName("New");
        jFileMenu.add(itemNew);

        itemExit = new JMenuItem("Exit");
        itemExit.setName("Exit");
        jFileMenu.add(itemExit);

        //Mode menu
        jModeMenu = new JMenu("Mode");
        jModeMenu.setName("MenuMode");
        jModeMenu.setMnemonic(KeyEvent.VK_M);
        jModeMenu.getAccessibleContext()
                .setAccessibleDescription("This mode menu has menu items");
        this.add(jModeMenu);

        itemVertex = new JMenuItem("Add a Vertex");
        itemVertex.setName("Add a Vertex");
        jModeMenu.add(itemVertex);

        itemEdge = new JMenuItem("Add an Edge");
        itemEdge.setName("Add an Edge");
        jModeMenu.add(itemEdge);

        jModeMenu.addSeparator();

        itemRemoveAVertex = new JMenuItem("Remove a Vertex");
        itemRemoveAVertex.setName("Remove a Vertex");
        jModeMenu.add(itemRemoveAVertex);

        itemRemoveAnEdge = new JMenuItem("Remove an Edge");
        itemRemoveAnEdge.setName("Remove an Edge");
        jModeMenu.add(itemRemoveAnEdge);

        jModeMenu.addSeparator();

        itemNone = new JMenuItem("None");
        itemNone.setName("None");
        jModeMenu.add(itemNone);


    }
}