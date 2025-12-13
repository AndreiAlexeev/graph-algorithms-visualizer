package visualizer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Graph extends JPanel {

    public interface VertexSelectionListener{
        void onVertexSelected(Vertex vertex);
    }

    private static final int EDGE_CLICK_TOLERANCE = 15;
    private static final int VERTEX_SPACING_MULTIPLIER = 3;
    private static final Logger LOGGER = Logger.getLogger(Graph.class.getName());
    private final List<Vertex> vertexList = new ArrayList<>();
    private final List<Edge> edgeList = new ArrayList<>();
    private final List<JLabel> labelList = new ArrayList<>();
    private Vertex firstSelectedVertex;
    private Vertex secondSelectedVertex;
    private Mode mode;
    private Vertex vertexMarkedForDeletion;
    private Edge edgeMarkedForDeletion;

    private transient VertexSelectionListener vertexSelectionListener;//the listeners are temporary
    private String pendingAlgorithm = null;

    public Graph() {
        setName("Graph");
        setLayout(null);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                LOGGER.log(Level.FINEST, "Mouse clicked at ({0}, {1})", new Object[]{e.getX(), e.getY()});
                LOGGER.log(Level.FINE, "Current mode: {0}", mode);

                if (mode == null) {
                    LOGGER.log(Level.WARNING, "Mode is null! Cannot process click.");
                    return;
                }
                switch (mode) {
                    case ADD_VERTEX:
                        addVertex(e);
                        break;

                    case ADD_EDGE:
                        addEdge(e);
                        break;

                    case REMOVE_A_VERTEX:
                        removeVertex(e);
                        break;

                    case REMOVE_AN_EDGE:
                        removeEdge(e);
                        break;

                    case NONE:
                        break;

                    case SELECT_START_VERTEX:
                        selectStartVertex(e);
                        break;
                }
            }
        });
    }

    //calculates distance between vertices
    private static int getDistance2Vertexes(int centerNewVertexX, int centerNewVertexY, Vertex v) {

        int coordinatesExistingVertexCenterX = v.getX() + v.getWidth() / 2;
        int coordinatesExistingVertexCenterY = v.getY() + v.getHeight() / 2;
        int difXBetweenVertexes = centerNewVertexX - coordinatesExistingVertexCenterX;
        int difYBetweenVertexes = centerNewVertexY - coordinatesExistingVertexCenterY;

        //patratul distantei dintre centrul noului vertex si centrul unui vertex existent
        return difXBetweenVertexes * difXBetweenVertexes + difYBetweenVertexes * difYBetweenVertexes;
    }

    //configures behavior
    public void setMode(Mode mode) {
        this.mode = mode;
        LOGGER.log(Level.FINEST, "Call setMode() method");
    }

    public void setVertexSelectionListener(VertexSelectionListener listener){
        this.vertexSelectionListener = listener;
    }

    public void setPendingAlgorithm(String algorithmName){
        this.pendingAlgorithm = algorithmName;
    }

    public String getPendingAlgorithm(){
        return pendingAlgorithm;
    }

    public List<Vertex> getVertexList(){
        return new ArrayList<>(vertexList);//a copy list
    }

    public List<Edge> getEdgeList(){
        return new ArrayList<>(edgeList);//a copy list
    }

    //the method that allows the new vertex center
    //validates the placement of vertices
    private boolean isAllowedVertex(int centerNewVertexX, int centerNewVertexY) {
        int radius = Vertex.getDefaultVertexSize() / 2;

        //if the peak fits within the panel's limits
        if (centerNewVertexX < radius || centerNewVertexX > getWidth() - radius) {
            return false;
        }
        if (centerNewVertexY < radius || centerNewVertexY > getHeight() - radius) {
            return false;
        }

        int minDistBetweenCentersVertices = radius * VERTEX_SPACING_MULTIPLIER;
        int threshold = minDistBetweenCentersVertices * minDistBetweenCentersVertices;

        //check the new point against all existing vertices
        for (Vertex v : vertexList) {
            int dist2 = getDistance2Vertexes(centerNewVertexX, centerNewVertexY, v);
            if (dist2 < threshold) {
                return false;
            }
        }
        return true;
    }

    //manage vertex addition
    public void addVertex(MouseEvent event) {

        int xCoordinate = event.getX();
        int yCoordinate = event.getY();

        Vertex clickedVertex = findVertexAtPosition(xCoordinate, yCoordinate);
        if (clickedVertex != null) {
            return;
        }

        //call the dialog box for a new vertex
        String nameVertex = DialogBox.askVertexName(Graph.this);
        if (nameVertex == null) {
            return;
        }

        for (Vertex vertex : vertexList) {
            if (("Vertex " + nameVertex).equals(vertex.getName())) {
                DialogBox.showMessage(Graph.this, "Vertex ID already exists!");
                return;
            }
        }

        Vertex newVertex = new Vertex(xCoordinate, yCoordinate, nameVertex);

        if (isAllowedVertex(xCoordinate, yCoordinate)) {
            //add a vertex if ...
            vertexList.add(newVertex);//add in list
            add(newVertex);//add in panel for visualization
            LOGGER.log(Level.FINEST, "Added a new vertex at: {0}", newVertex.getBounds());
            revalidate();
            repaint();
        } else {
            LOGGER.log(Level.WARNING, "Is too close to another vertex or is outside the panel!");
        }
    }

    private void selectFirstVertex(Vertex clickedVertex) {
        this.firstSelectedVertex = clickedVertex;
        clickedVertex.setVertexSelected(true);
//            DialogBox.showMessage(Graph.this, "You have selected first Vertex");
    }

    //manage edge addition
    public void addEdge(MouseEvent event) {

        LOGGER.log(Level.FINE, "addEdge() called - mode: {0}, firstVertex: {1}",
                new Object[]{this.mode, firstSelectedVertex});

        Vertex clickedVertex =
                findVertexAtPosition(event.getX(), event.getY());//get the coordinates where I clicked

        if (clickedVertex == null) {
//            DialogBox.showMessage(Graph.this, "Select a vertex");
            resetSelectedVertices();
            return;
        }

        //first Vertex to draw an Edge
        if (this.firstSelectedVertex == null) {
            this.firstSelectedVertex = clickedVertex;
            clickedVertex.setVertexSelected(true);
//            DialogBox.showMessage(Graph.this, "You have selected first Vertex");
        } else {
            if (clickedVertex == firstSelectedVertex) {
                DialogBox.showMessage(Graph.this, "Search another vertex");
                resetSelectedVertices();
                return;
            }

            //second vertex to draw an Edge
            this.secondSelectedVertex = clickedVertex;
            this.secondSelectedVertex.setVertexSelected(true);

            String weightStr = DialogBox.askEdgeWeight(Graph.this);

            if (weightStr == null) {
//                DialogBox.showMessage(Graph.this, "Search second vertex!");
                resetSelectedVertices();
                return;
            }

            for (Edge edge : edgeList) {
                if ((edge.fromVertex.equals(firstSelectedVertex) &&
                        edge.toVertex.equals(secondSelectedVertex))) {
//                    DialogBox.showMessage(Graph.this, "Edge already exists!");

                    //reset selected vertexes
                    resetSelectedVertices();
                    return;
                }
            }

            int weight = Integer.parseInt(weightStr);

            Edge edgeTur = new Edge(firstSelectedVertex, secondSelectedVertex, weight);
            Edge edgeRetur = new Edge(secondSelectedVertex, firstSelectedVertex, weight);

            JLabel labelTur = edgeTur.createEdgeLabel();//

            //add in lists
            edgeList.add(edgeTur);
            edgeList.add(edgeRetur);
            labelList.add(labelTur);

            //add in panel
            add(edgeTur);
            add(edgeRetur);
            add(labelTur);//
//            add(labelRetur);//

            setComponentZOrder(labelTur, 0);

            revalidate();
            repaint();

            //reset selected vertexes
            resetSelectedVertices();
        }
    }

    //check if a point is inside the vertex
    private boolean isInsideVertex(double clickX, double clickY, double centerX, double centerY, double radius) {
        return (Math.pow((clickX - centerX), 2) +
                Math.pow((clickY - centerY), 2)) <=
                Math.pow(radius, 2);
    }

    //determine which vertex was clicked
    private Vertex findVertexAtPosition(int clickX, int clickY) {

        for (Vertex vertex : vertexList) {
            //position in the screen
            int vertexX = vertex.getX();
            int vertexY = vertex.getY();
            //dimension of vertex
            int vertexWidth = vertex.getWidth();
            int vertexHeight = vertex.getHeight();
            //center of the circle
            int centerVertexX = vertexX + vertexWidth / 2;
            int centerVertexY = vertexY + vertexHeight / 2;

            int radius = vertexHeight / 2;

            //determine whether the point is inside the circle
            if (isInsideVertex(clickX, clickY, centerVertexX, centerVertexY, radius)) {
                return vertex;
            }
        }
        return null;
    }

    //reset selection status
    private void resetSelectedVertices() {
//        System.out.println("Resetare: firstSelectedVertex: " + firstSelectedVertex.getName());

        //reset selected vertexes
        if (this.firstSelectedVertex != null) {
            this.firstSelectedVertex.setVertexSelected(false);
            this.firstSelectedVertex = null;
        }

        if (this.secondSelectedVertex != null) {
            this.secondSelectedVertex.setVertexSelected(false);
            this.secondSelectedVertex = null;
        }

        LOGGER.log(Level.FINEST, "Complete reset.");
    }

    //manage vertex deletion
    public void removeVertex(MouseEvent event) {
        //get the coordinates where I clicked
        int clickX = event.getX();
        int clickY = event.getY();
//        boolean response;

        Vertex clickedVertex = findVertexAtPosition(clickX, clickY);

        if (clickedVertex == null) {
            DialogBox.showMessage(Graph.this, "Select a vertex");
            return;
        }

        this.vertexMarkedForDeletion = clickedVertex;
        clickedVertex.setVertexSelected(true);
//        response = DialogBox.confirmVertexDeletion(Graph.this,
//                vertexMarkedForDeletion.getName());

//        if (response) {
        Iterator<Vertex> vertexIterator = vertexList.iterator();
        while (vertexIterator.hasNext()) {

            Vertex currentVertex = vertexIterator.next();

            if (currentVertex.equals(vertexMarkedForDeletion)) {//*
                vertexIterator.remove();//remove from the list
                remove(currentVertex);//remove from the panel

                LOGGER.log(Level.INFO, "You have successfully deleted the {0}",
                        currentVertex.getBounds());
                break;
            }
        }

        Iterator<Edge> edgeIterator = edgeList.iterator();
        while (edgeIterator.hasNext()) {
            Edge currentEdge = edgeIterator.next();

            if (currentEdge.fromVertex == vertexMarkedForDeletion ||
                    currentEdge.toVertex == vertexMarkedForDeletion) {
                remove(currentEdge);//remove from panel

                deleteLabelForEdge(currentEdge.fromVertex, currentEdge.toVertex);

                edgeIterator.remove();//remove from th list
            }
        }
        revalidate();
        repaint();
//        } else {
//            clickedVertex.setVertexSelected(false);
//            vertexMarkedForDeletion = null;
//        }
    }

    //check if a point is near the edge (standard algorithm)
    private boolean isNearEdge(double clickX, double clickY,
                               double startX, double startY,
                               double endX, double endY) {
        //calculate the vectors
        double clickVectorX = clickX - startX;
        double clickVectorY = clickY - startY;
        double lineVectorX = endX - startX;
        double lineVectorY = endY - startY;

        //calculate how much the vectors align
        double alignment = clickVectorX * lineVectorX + clickVectorY * lineVectorY;
        double lineLengthSquared = lineVectorX * lineVectorX + lineVectorY * lineVectorY;

        //calculate the percentage on the line (0 is start, end is 1)
        double percentageOnLine = (lineLengthSquared == 0) ? -1 : (alignment / lineLengthSquared);

        //punctul cel mai apropia cu coordonatele x si y
        double closestPointX;
        double closestPointY;

        if (percentageOnLine < 0) {
            //the closest point is the start (before the line).
            closestPointX = startX;
            closestPointY = startY;
        } else if (percentageOnLine > 1) {
            //the closest point is the end (after the line).
            closestPointX = endX;
            closestPointY = endY;
        } else {
            //projection on the line between start and end.
            closestPointX = startX + percentageOnLine * lineVectorX;
            closestPointY = startY + percentageOnLine * lineVectorY;
        }

        //calculate the distance from the click to the nearest point
        double distanceX = clickX - closestPointX;
        double distanceY = clickY - closestPointY;
        double finalDistance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        return finalDistance <= EDGE_CLICK_TOLERANCE;
    }

    //determine which edge was clicked
    private Edge findEdgeAtPosition(int clickX, int clickY) {

        LOGGER.log(Level.FINER, "Searching edge at ({0}, {1}), total edges: {2}",
                new Object[]{clickX, clickY, edgeList.size()});

        for (Edge edge : edgeList) {

            LOGGER.log(Level.FINEST, "Testing edge {0}", edge.getName());

            if (isNearEdge(clickX, clickY,
                    edge.startX, edge.startY,
                    edge.endX, edge.endY)) {

                LOGGER.log(Level.FINE, "Found edge {0} ", edge.getName());//*
                return edge;
            }/* else {
                LOGGER.log(Level.INFO, "Not near this edge");
            }*/
        }
        LOGGER.log(Level.FINE, "No edge found at click position");//*
        return null;
    }

    //reset selection status
    private void resetSelectedEdge() {
        //reset selected edges
        if (edgeMarkedForDeletion != null) {
            this.edgeMarkedForDeletion.setEdgeSelected(false);
            this.edgeMarkedForDeletion = null;
            LOGGER.log(Level.FINEST, "Edge selection reset complete");
        }
    }

    private Edge findInverseEdge(Edge originalEdge) {
        for (Edge edge : edgeList) {
            if (originalEdge.fromVertex == edge.toVertex &&
                    originalEdge.toVertex == edge.fromVertex) {
                return edge;
            }
        }
        return null;
    }

    public void removeEdge(MouseEvent event) {
        //get the coordinates where I clicked
        int clickX = event.getX();
        int clickY = event.getY();
//        boolean edgeResponse;

        Edge clickedEdge = findEdgeAtPosition(clickX, clickY);

        if (clickedEdge == null) {
            DialogBox.showMessage(Graph.this, "Select an edge");
            return;
        }

        this.edgeMarkedForDeletion = clickedEdge;
        clickedEdge.setEdgeSelected(true);
//        edgeResponse = DialogBox.confirmEdgeDeletion(Graph.this,
//                edgeMarkedForDeletion.getName());

//        if (edgeResponse) {
        Edge inverseEdge = findInverseEdge(clickedEdge);

        edgeList.remove(clickedEdge);//remove from the list
        if (inverseEdge != null) {
            edgeList.remove(inverseEdge);
        }

        remove(clickedEdge);//remove from the panel
        if (inverseEdge != null) {
            remove(inverseEdge);
        }

        deleteLabelForEdge(clickedEdge.fromVertex, clickedEdge.toVertex);
        deleteLabelForEdge(clickedEdge.toVertex, clickedEdge.fromVertex);

        revalidate();
        repaint();
        edgeMarkedForDeletion = null;
//        }else {
//            resetSelectedEdge();
//        }
    }

    //method for deleting a label when an edge is deleted
    public void deleteLabelForEdge(Vertex firstVertexID, Vertex secondVertexID) {
        JLabel labelToRemove = null;
        for (JLabel label : labelList) {
            Pattern pattern = Pattern.compile(
                    "EdgeLabel <" + firstVertexID.getVertexId() +
                            " -> " + secondVertexID.getVertexId() +
                            ">");
            Matcher matcher = pattern.matcher(label.getName());
            boolean matchFound = matcher.find();
            if (matchFound) {
                labelToRemove = label;
                break;
            }
        }

        if (labelToRemove != null) {
            labelList.remove(labelToRemove);
            remove(labelToRemove);
        }
    }

    public void resetGraph() {
        removeAll();
        vertexList.clear();
        edgeList.clear();
        labelList.clear();
        resetSelectedVertices();
        resetSelectedEdge();
        vertexMarkedForDeletion = null;
        edgeMarkedForDeletion = null;
        revalidate();
        repaint();
    }

    public void resetAllSelections() {
        if (firstSelectedVertex != null) {
            firstSelectedVertex.setVertexSelected(false);
            firstSelectedVertex = null;
        }

        if (secondSelectedVertex != null) {
            secondSelectedVertex.setVertexSelected(false);
            secondSelectedVertex = null;
        }

        if (vertexMarkedForDeletion != null) {
            vertexMarkedForDeletion.setVertexSelected(false);
            vertexMarkedForDeletion = null;
        }

        if (edgeMarkedForDeletion != null) {
            edgeMarkedForDeletion.setEdgeSelected(false);
            edgeMarkedForDeletion = null;
        }
    }

    private void selectStartVertex(MouseEvent event){
        int clickX = event.getX();
        int clickY = event.getY();

        Vertex clickedVertex = findVertexAtPosition(clickX, clickY);

        if(clickedVertex == null){
            DialogBox.showMessage(Graph.this, "Select a vertex");
            return;
        }

        System.out.println("Selected start vertex: " + clickedVertex.getName());

        //notify the listener if there is
        if(vertexSelectionListener != null && pendingAlgorithm != null){
            vertexSelectionListener.onVertexSelected(clickedVertex);
        }

//        pendingAlgorithm = null;//reset pending algorithm
    }
}