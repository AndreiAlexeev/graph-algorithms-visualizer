package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MainFrame extends JFrame {

    Graph graph = new Graph();
    JLabel jLabelMode;
    Mode mode;
    AppMenuBar appMenuBar = new AppMenuBar();


    public MainFrame() {
        initUI();//what she has seen
        initMenuAction();//what she does
    }

    private void initUI() {
        int panelWidth = 800;
        int panelHeight = 600;

        graph.setLayout(null);
        graph.setPreferredSize(new Dimension(panelWidth, panelHeight));
        graph.setBackground(Color.DARK_GRAY.darker());
        ComponentListener componentListener = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionLabel();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO document why this method is empty
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // TODO document why this method is empty
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO document why this method is empty
            }

        };
        graph.addComponentListener(componentListener);

        jLabelMode = new JLabel();
        jLabelMode.setName("Mode");
        jLabelMode.setText("Current mode -> Add a Vertex");
        jLabelMode.setFont(new Font("EB Garamond", Font.BOLD, 14));
        jLabelMode.setForeground(Color.WHITE);
        jLabelMode.setOpaque(false);

        setLayout(null);
        graph.setBounds(0, 0, panelWidth, panelHeight);
        add(graph);
        add(jLabelMode);
        setSize(panelWidth, panelHeight);

        setTitle("Graph-Algorithms Visualizer");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(appMenuBar);

        setVisible(true);
        SwingUtilities.invokeLater(() -> positionLabel());
        updateMode(Mode.ADD_VERTEX);
    }

    private void updateMode(Mode currentMode) {
        this.mode = currentMode;
        jLabelMode.setText("Current mode -> " + currentMode.getDisplayName());
        positionLabel();
        graph.setMode(currentMode);
        graph.resetAllSelections();
    }

    public void initMenuAction() {
        this.appMenuBar.itemVertex.addActionListener(e -> updateMode(Mode.ADD_VERTEX));

        this.appMenuBar.itemEdge.addActionListener(e -> updateMode(Mode.ADD_EDGE));

        this.appMenuBar.itemRemoveAVertex.addActionListener(e -> updateMode(Mode.REMOVE_A_VERTEX));

        this.appMenuBar.itemRemoveAnEdge.addActionListener(e -> updateMode(Mode.REMOVE_AN_EDGE));

        this.appMenuBar.itemNone.addActionListener(e -> updateMode(Mode.NONE));

        this.appMenuBar.itemNew.addActionListener(e -> graph.resetGraph());

        this.appMenuBar.itemExit.addActionListener(e -> closeApp());
    }

    private void positionLabel() {
        int x;
        int y;
        int margin = 15;
        jLabelMode.setSize(jLabelMode.getPreferredSize());
        x = graph.getWidth() - jLabelMode.getWidth() - margin;
        y = margin;
        jLabelMode.setLocation(x, y);
    }

    private void closeApp(){
        System.exit(0);
    }
}