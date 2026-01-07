package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private static final int LABEL_MARGIN = 15;
    private static final int GRAPH_WIDTH = 800;
    private static final int GRAPH_HEIGHT = 600;
    private static final String DIJKSTRA_STRING = "Dijkstra";
    private final Graph graph = new Graph();
    private final AppMenuBar appMenuBar = new AppMenuBar();
    private JLabel jLabelMode;
    private JLabel algorithmStatusLabel;

    public MainFrame() {
        initUI();//what she has seen
        initMenuAction();//what she does
        setupGraphListener();
    }

    private void initUI() {

        graph.setLayout(null);
        graph.setPreferredSize(new Dimension(GRAPH_WIDTH, GRAPH_HEIGHT));
        graph.setBackground(Color.DARK_GRAY.darker());

        ComponentAdapter componentAdapter = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionModeLabel();
                if (algorithmStatusLabel.isVisible()) {//only positions it on the surface if the variable is visible
                    positionDisplayLabel();
                }
            }
        };

        graph.addComponentListener(componentAdapter);

        //Mode label
        jLabelMode = new JLabel();
        jLabelMode.setName("Mode");
        jLabelMode.setText("Current mode -> Add a Vertex");
        jLabelMode.setFont(new Font("EB Garamond", Font.BOLD, 14));
        jLabelMode.setForeground(Color.WHITE);
        jLabelMode.setOpaque(false);

        //algoritm status label
        algorithmStatusLabel = new JLabel();
        algorithmStatusLabel.setName("Display");
        algorithmStatusLabel.setText("");
        algorithmStatusLabel.setFont(new Font("EB Garamond", Font.BOLD, 14));
        algorithmStatusLabel.setForeground(Color.WHITE);
        algorithmStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        algorithmStatusLabel.setOpaque(false);
        algorithmStatusLabel.setVisible(false);

        setLayout(null);
        graph.setBounds(0, 0, GRAPH_WIDTH, GRAPH_HEIGHT);
        add(algorithmStatusLabel);
        add(jLabelMode);
        add(graph);
        setSize(GRAPH_WIDTH, GRAPH_HEIGHT + 55);

        setTitle("Graph-Algorithms Visualizer");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(appMenuBar);

        setVisible(true);
        SwingUtilities.invokeLater(this::positionModeLabel);
        updateMode(Mode.ADD_VERTEX);
    }

    private void updateMode(Mode currentMode) {
        jLabelMode.setText("Current mode -> " + currentMode.getDisplayName());
        positionModeLabel();
        graph.setMode(currentMode);
        graph.resetAllSelections();
    }

    public void initMenuAction() {
        this.appMenuBar
                .itemVertex
                .addActionListener(e -> updateMode(Mode.ADD_VERTEX));

        this.appMenuBar
                .itemEdge
                .addActionListener(e -> updateMode(Mode.ADD_EDGE));

        this.appMenuBar
                .itemRemoveAVertex
                .addActionListener(e -> updateMode(Mode.REMOVE_A_VERTEX));

        this.appMenuBar
                .itemRemoveAnEdge
                .addActionListener(e -> updateMode(Mode.REMOVE_AN_EDGE));

        this.appMenuBar
                .itemNone
                .addActionListener(e -> updateMode(Mode.NONE));

        this.appMenuBar
                .itemNew
                .addActionListener(e -> {
                    graph.resetGraph();
                    algorithmStatusLabel.setVisible(false);
                });

        this.appMenuBar
                .itemExit
                .addActionListener(e -> closeApp());
        //new items in stage 5/7
        this.appMenuBar
                .itemDFS
                .addActionListener(e -> {
                    updateMode(Mode.SELECT_START_VERTEX);
                    graph.setPendingAlgorithm("DFS");
                    algorithmStatusLabel.setText("Please choose a starting vertex!");
                    algorithmStatusLabel.setVisible(true);
                    positionDisplayLabel();


                    System.out.println("Label text: " + algorithmStatusLabel.getText());
                    System.out.println("Label visible: " + algorithmStatusLabel.isVisible());
                    System.out.println("Label bounds: " + algorithmStatusLabel.getBounds());
                });

        this.appMenuBar
                .itemBFS
                .addActionListener(e -> {
                    updateMode(Mode.SELECT_START_VERTEX);
                    graph.setPendingAlgorithm("BFS");
                    algorithmStatusLabel.setText("Please choose a starting vertex!");
                    algorithmStatusLabel.setVisible(true);
                    positionDisplayLabel();

                });

        this.appMenuBar
                .itemDijkstra
                .addActionListener(e -> {
                    updateMode(Mode.SELECT_START_VERTEX);
                    graph.setPendingAlgorithm(DIJKSTRA_STRING);
                    algorithmStatusLabel.setText("Please choose a starting vertex!");
                    algorithmStatusLabel.setVisible(true);
                    positionDisplayLabel();
                });

        // stage7/7
        this.appMenuBar
                .itemPrims
                .addActionListener(e -> {
                    updateMode(Mode.SELECT_START_VERTEX);
                    graph.setPendingAlgorithm("Prim's Algorithm");
                    algorithmStatusLabel.setText("Please choose a starting vertex!");
                    algorithmStatusLabel.setVisible(true);
                    positionDisplayLabel();
                });
    }

    private void positionModeLabel() {
        jLabelMode.setSize(jLabelMode.getPreferredSize());
        int x = graph.getWidth() - jLabelMode.getWidth() - LABEL_MARGIN;
        int y = LABEL_MARGIN;
        jLabelMode.setLocation(x, y);
    }

    private void positionDisplayLabel() {
        algorithmStatusLabel.setSize(algorithmStatusLabel.getPreferredSize());
        int x = (graph.getWidth() - algorithmStatusLabel.getWidth()) / 2;
        int y = graph.getHeight() - algorithmStatusLabel.getHeight() - LABEL_MARGIN;
        System.out.println("graph.getHeight() = " + graph.getHeight());
        System.out.println("label height = " + algorithmStatusLabel.getHeight());
        System.out.println("Calculated y = " + y);
        algorithmStatusLabel.setLocation(x, y);
    }

    private void executeAlgorithm(Vertex startVertex) {
        System.out.println("executeAlgorithm called with: " + startVertex.getName());
        String selectedAlgorithm = graph.getPendingAlgorithm();

        if (selectedAlgorithm == null) {
            System.out.println("Algorithm is null, returning!");
            return;
        }

        updateMode(Mode.NONE);
        algorithmStatusLabel.setText("Please wait...");
        System.out.println("Set label to Please wait!");
        algorithmStatusLabel.repaint();


        Timer timer = new Timer(1000, e -> {
            List<Vertex> vertices = graph.getVertexList();
            List<Edge> edges = graph.getEdgeList();
            List<String> result = List.of();//după implementarea metodei de conversie din map in list,
            //trebuie șters list.of().


            switch (selectedAlgorithm) {
                case "DFS" -> result = GraphAlgorithms.depthFirstSearch(startVertex, vertices, edges);
                case "BFS" -> result = GraphAlgorithms.breadthFirstSearch(startVertex, vertices, edges);
                case DIJKSTRA_STRING -> {
                    Map<Vertex, Integer> distances =
                            GraphAlgorithms.dijkstraAlgorithm(startVertex, vertices, edges);
                    result = convertMapToList(distances);
                }
                case "Prim's Algorithm" -> {

                    Map<String, String> map =
                            GraphAlgorithms.primAlgorithm(startVertex, vertices, edges);
                    result = formatPrimAlgResult(map);
                }
                default -> throw new IllegalStateException("Unknow algorithm: " + selectedAlgorithm);
            }

            String resultOfAlgorithm = formatAlgorithmOutput(selectedAlgorithm, result);

            algorithmStatusLabel.setText(resultOfAlgorithm);
            positionDisplayLabel();
            System.out.println("Final result: " + resultOfAlgorithm);
            System.out.println("Label visible after: " + algorithmStatusLabel.isVisible());
            algorithmStatusLabel.repaint();
            repaint();
        });

        timer.setRepeats(false);
        timer.start();
    }

    private void setupGraphListener() {
        graph.setVertexSelectionListener(vertex ->
                executeAlgorithm(vertex));
    }

    private void closeApp() {
        System.exit(0);
    }

    private List<String> convertMapToList(Map<Vertex, Integer> map) {
        List<String> result = new ArrayList<>();

        for (Map.Entry<Vertex, Integer> entry : map.entrySet()) {
            result.add(entry.getKey().getVertexId() + "=" + entry.getValue());
        }
        return result;
    }

    private String formatAlgorithmOutput(String algorithmName, List<String> result) {
        // Alege separator-ul bazat pe algoritm
        String separator =
                (algorithmName.equals(DIJKSTRA_STRING) || algorithmName.equals("Prim's Algorithm")) ? ", " : " -> ";

        StringBuilder output = new StringBuilder(algorithmName + " : ");

        if(!algorithmName.equals("Prim's Algorithm")){
            output.append(algorithmName).append(" : ");
        }

        boolean first = true;
        for (String s : result) {
            if (!first) {
                output.append(separator);
            }
            output.append(s);
            first = false;
        }
        return output.toString();
    }

    private List<String> formatPrimAlgResult(Map<String, String> primResult) {

        List<String> keys = new ArrayList<>(primResult.keySet());
        Collections.sort(keys);

        List<String> resultPrimAlgorithm = new ArrayList<>();
        for (String key : keys) {
            resultPrimAlgorithm.add(key + "=" + primResult.get(key));
        }
        return resultPrimAlgorithm;
    }
}