package visualizer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Graph extends JPanel {
    private static final int EDGE_CLICK_TOLERANCE = 15;
    private final List<Vertex> vertexList = new ArrayList<>();
    private final List<Edge> edgeList = new ArrayList<>();
    private final List<JLabel> labelList = new ArrayList<>();
    private Vertex firstSelectedVertex;
    private Vertex secondSelectedVertex;
    private Mode mode;
    private Vertex vertexMarkedForDeletion;
    private Edge edgeMarkedForDeletion;

    public Graph() {
        setName("Graph");
        setLayout(null);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                System.out.println("mouse clicked at " + e.getX() + ", " + e.getY());
                System.out.println("current mode: " + mode);

                if (mode == null){
                    System.out.println("warning: mode is null!");
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
        System.out.println("Call setMode");
    }

    //data access
    public List<Vertex> getVertexList() {
        return vertexList;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    //the method that allows the new vertex center
    //validates the placement of vertices
    private boolean isAllowedVertex(int centerNewVertexX, int centerNewVertexY) {
        int radius = Vertex.getVertexSize() / 2;

        //if the peak fits within the panel's limits
        if (centerNewVertexX < radius || centerNewVertexX > getWidth() - radius) {
            return false;
        }
        if (centerNewVertexY < radius || centerNewVertexY > getHeight() - radius) {
            return false;
        }

        int minDistBetweenCentersVertices = radius * 3;
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
        if (clickedVertex != null){
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

        Vertex newVertex = new Vertex(xCoordinate, yCoordinate, 50, nameVertex);

        if (isAllowedVertex(xCoordinate, yCoordinate)) {
            //add a vertex if ...
            vertexList.add(newVertex);//add in list
            add(newVertex);//add in panel for visualization
            System.out.println("added " + newVertex.getBounds());//for verification
            revalidate();
            repaint();
        } else {
            System.out.println("Is too close to another vertex or is outside the panel");
        }
    }

    //manage edge addition
    public void addEdge(MouseEvent event) {
        System.out.println("addEdge called");
        System.out.println("current mode in Graph: " + this.mode);
        System.out.println(" firstSelectedVertex: " + firstSelectedVertex);

        //get the coordinates where I clicked
        int clickX = event.getX();
        int clickY = event.getY();

        Vertex clickedVertex = findVertexAtPosition(clickX, clickY);

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
                if ((edge.fromVertex1.equals(firstSelectedVertex) &&
                        edge.toVertex2.equals(secondSelectedVertex))) {
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
//            JLabel labelRetur = edgeRetur.createEdgeLabel();//

            //add in lists
            edgeList.add(edgeTur);
            edgeList.add(edgeRetur);
            labelList.add(labelTur);
//            labelList.add(labelRetur);
            //add in panel
            add(edgeTur);
            add(edgeRetur);
            add(labelTur);//
//            add(labelRetur);//

            setComponentZOrder(labelTur, 0);
//            setComponentZOrder(labelRetur, 1);

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

        System.out.println("Resetare completa");
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
                    System.out.println("You have successfully deleted the " +
                            currentVertex.getBounds());//for verification
                    break;
                }
            }

            Iterator<Edge> edgeIterator = edgeList.iterator();
            while(edgeIterator.hasNext()){
                Edge currentEdge = edgeIterator.next();

                if (currentEdge.fromVertex1 == vertexMarkedForDeletion ||
                        currentEdge.toVertex2 == vertexMarkedForDeletion){
                    remove(currentEdge);//remove from panel

                    deleteLabelForEdge(currentEdge.fromVertex1, currentEdge.toVertex2);

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
        System.out.println("findEdgeAtPosition called");
        System.out.println("click coords: " + clickX + ", " + clickY);
        System.out.println("edgeListe size " + edgeList.size());

        for (Edge edge : edgeList) {
            System.out.println("testing edge " + edge.getName());
            System.out.println("edge coords " + edge.startX + ", " + edge.startY);
            if (isNearEdge(clickX, clickY,
                    edge.startX, edge.startY,
                    edge.endX, edge.endY)) {
                System.out.println("found edge!");
                return edge;
            } else {
                System.out.println("not near this edge");
            }
        }
        System.out.println("no edge found");
        return null;
    }

    //reset selection status
    private void resetSelectedEdge() {
        //reset selected edges
        if (edgeMarkedForDeletion != null) {
            this.edgeMarkedForDeletion.setEdgeSelected(false);
            this.edgeMarkedForDeletion = null;
            System.out.println("Resetare Edge completa");
        }
    }

    private Edge findInverseEdge(Edge originalEdge) {
        for (Edge edge : edgeList) {
            if (originalEdge.fromVertex1 == edge.toVertex2 &&
                    originalEdge.toVertex2 == edge.fromVertex1) {
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

            deleteLabelForEdge(clickedEdge.fromVertex1, clickedEdge.toVertex2);
            deleteLabelForEdge(clickedEdge.toVertex2, clickedEdge.fromVertex1);

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
                            " -> " + secondVertexID.getVertexId() + ">");
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

    public void resetGraph(){
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

    public void resetAllSelections(){
        if (firstSelectedVertex != null){
            firstSelectedVertex.setVertexSelected(false);
            firstSelectedVertex = null;
        }

        if (secondSelectedVertex != null){
            secondSelectedVertex. setVertexSelected(false);
            secondSelectedVertex = null;
        }

        if (vertexMarkedForDeletion != null){
            vertexMarkedForDeletion.setVertexSelected(false);
            vertexMarkedForDeletion = null;
        }

        if (edgeMarkedForDeletion != null){
            edgeMarkedForDeletion.setEdgeSelected(false);
            edgeMarkedForDeletion = null;
        }
    }
}