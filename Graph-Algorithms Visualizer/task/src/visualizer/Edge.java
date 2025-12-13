package visualizer;

import javax.swing.*;
import java.awt.*;

public class Edge extends JComponent {
    private static final int LABEL_MIN_WIDTH = 75;
    private static final int LABEL_MIN_HEIGHT = 25;
    private static final int LABEL_FONT_SIZE = 25;
    private static final int SECTION_INSET = 3;
    private static final int SELECTION_BORDER_RADIUS = 3;
    final Vertex fromVertex;
    final Vertex toVertex;
    final int startX;
    final int startY;
    final int endX;
    final int endY;

    public int getEdgeWeight() {
        return edgeWeight;
    }

    private final int edgeWeight;
    private boolean isEdgeSelected;

    public Edge(Vertex startVertex, Vertex endVertex, int edgeWeight) {
        this.fromVertex = startVertex;
        this.toVertex = endVertex;
        this.edgeWeight = edgeWeight;

        //extracte vertex ID
        String firstVertexID = fromVertex.getVertexId();
        String secondVertexID = toVertex.getVertexId();

        //absolute calculation in the Graph
        startX = fromVertex.getX() + fromVertex.getWidth() / 2;
        startY = fromVertex.getY() + fromVertex.getHeight() / 2;
        endX = toVertex.getX() + toVertex.getWidth() / 2;
        endY = toVertex.getY() + toVertex.getHeight() / 2;

        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.max(Math.abs(endX - startX), LABEL_MIN_WIDTH);//for a text
        int height = Math.max(Math.abs(endY - startY), LABEL_MIN_HEIGHT);//font size

        this.setBounds(x, y, width, height);
        this.setName("Edge <" + firstVertexID + " -> " + secondVertexID + ">");

        setVisible(true);
        setOpaque(true);
    }

    public void setEdgeSelected(boolean edgeSelected) {
        this.isEdgeSelected = edgeSelected;
        repaint();
    }

    //rendering
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;

        GraphicsUtils.doDrawing(graphics2D);
        graphics2D.setColor(Color.white);
        graphics2D.setFont(new Font("EB Garamond", Font.PLAIN, LABEL_FONT_SIZE));
        //relative coordinates (relative to Edge)
        int relativeStartX = startX - this.getX();
        int relativeStartY = startY - this.getY();
        int relativeEndX = endX - this.getX();
        int relativeEndY = endY - this.getY();

        //complexity for selected edges
        if (isEdgeSelected) {
            graphics2D.setColor(Color.RED);

            int minX = Math.min(relativeStartX, relativeEndX) - SECTION_INSET;
            int minY = Math.min(relativeStartY, relativeEndY) - SECTION_INSET;
            int width = Math.abs(relativeEndX - relativeStartX) + 2 * SECTION_INSET;
            int height = Math.abs(relativeEndY - relativeStartY) + 2 * SECTION_INSET;

            graphics2D.drawRoundRect(minX, minY, width, height, SELECTION_BORDER_RADIUS, SELECTION_BORDER_RADIUS);
        }

        graphics2D.drawLine(relativeStartX, relativeStartY, relativeEndX, relativeEndY);
    }

    //method for create label on edge
    public JLabel createEdgeLabel() {
        String firstVertexID = fromVertex.getVertexId();
        String secondVertexID = toVertex.getVertexId();

        JLabel edgeLabel = new JLabel(String.valueOf(edgeWeight));
        edgeLabel.setName("EdgeLabel <" + firstVertexID + " -> " + secondVertexID + ">");

        edgeLabel.setFont(new Font("EB Garamond", Font.PLAIN, LABEL_FONT_SIZE));
        edgeLabel.setForeground(Color.WHITE);
        edgeLabel.setOpaque(true);
        edgeLabel.setBackground(Color.DARK_GRAY.darker());

        //calculate the center of the line with absolute coordinates
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;

        edgeLabel.setSize(edgeLabel.getPreferredSize());

        //center the label on the midpoint of the line
        int labelX = midX - edgeLabel.getWidth() / 2;
        int labelY = midY - edgeLabel.getHeight() / 2;
        edgeLabel.setLocation(labelX, labelY);

        return edgeLabel;
    }
}