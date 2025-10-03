package visualizer;

import javax.swing.*;
import java.awt.*;

public class Edge extends JComponent {
    Vertex fromVertex1;
    Vertex toVertex2;
    int edgeWeight;

    int startX;
    int startY;
    int endX;
    int endY;

    boolean isEdgeSelected;

    public Edge(Vertex startVertex, Vertex endVertex, int edgeWeight) {
        this.fromVertex1 = startVertex;
        this.toVertex2 = endVertex;
        this.edgeWeight = edgeWeight;

        //extracte vertex ID
        String firstVertexID = fromVertex1.getVertexId();
        String secondVertexID = toVertex2.getVertexId();

        //absolute calculation in the Graph
        startX = fromVertex1.getX() + fromVertex1.getWidth() / 2;
        startY = fromVertex1.getY() + fromVertex1.getHeight() / 2;
        endX = toVertex2.getX() + toVertex2.getWidth() / 2;
        endY = toVertex2.getY() + toVertex2.getHeight() / 2;

        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.max(Math.abs(endX - startX), 75);//for a text
        int height = Math.max(Math.abs(endY - startY), 25);//font size

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
        graphics2D.setFont(new Font("EB Garamond", Font.PLAIN, 25));
        //relative coordinates (relative to Edge)
        int relativeStartX = startX - this.getX();
        int relativeStartY = startY - this.getY();
        int relativeEndX = endX - this.getX();
        int relativeEndY = endY - this.getY();

        //complexity for selected edges
        if (isEdgeSelected) {
            graphics2D.setColor(Color.RED);
            int inset = 3;

            int minX = Math.min(relativeStartX, relativeEndX) - inset;
            int minY = Math.min(relativeStartY, relativeEndY) - inset;
            int width = Math.abs(relativeEndX - relativeStartX) + 2 * inset;
            int height = Math.abs(relativeEndY - relativeStartY) + 2 * inset;

            graphics2D.drawRoundRect(minX, minY, width, height, 3, 3);
        }

        graphics2D.drawLine(relativeStartX, relativeStartY, relativeEndX, relativeEndY);
    }

    //method for create label on edge
    public JLabel createEdgeLabel() {
        String firstVertexID = fromVertex1.getVertexId();
        String secondVertexID = toVertex2.getVertexId();

        JLabel edgeLabel = new JLabel(String.valueOf(edgeWeight));
        edgeLabel.setName("EdgeLabel <" + firstVertexID + " -> " + secondVertexID + ">");

        edgeLabel.setFont(new Font("EB Garamond", Font.PLAIN, 25));
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