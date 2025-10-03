package visualizer;

import javax.swing.*;
import java.awt.*;

public class Vertex extends JPanel {
    static int vertexSize;
    static int vertexCounter = 0;
    int xVertex;
    int yVertex;
    boolean isVertexSelected;

    public Vertex(int x, int y, int vertexSize, String strValue) {
        this.xVertex = x - vertexSize / 2;
        this.yVertex = y - vertexSize / 2;

        Vertex.setVertexSize(vertexSize);
        setName("Vertex " + strValue);
        setBounds(xVertex, yVertex, vertexSize, vertexSize);
        setPreferredSize(new Dimension(vertexSize, vertexSize));
        setVisible(true);
        setOpaque(false);

        JLabel jLabel = new JLabel();
        jLabel.setName("VertexLabel " + strValue);
        jLabel.setText(String.valueOf(strValue));
        this.setLayout(new BorderLayout());
        jLabel.setFont(new Font("EB Garamond", Font.PLAIN, 25));
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(jLabel, BorderLayout.CENTER);

        vertexCounter++;
    }

    public static int getVertexSize() {
        return vertexSize;
    }

    public static void setVertexSize(int vertexSize) {
        Vertex.vertexSize = vertexSize;
    }

    public String getVertexId() {
        return this.getName().substring(7);
    }

    public void setVertexSelected(boolean vertexSelected) {
        this.isVertexSelected = vertexSelected;
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;
        GraphicsUtils.doDrawing(graphics2D);
        graphics2D.setColor(Color.GREEN);
        int w = getWidth();
        int h = getHeight();
        graphics2D.fillOval(0, 0, w, h);

        //complexity for selected vertexes
        if (isVertexSelected) {
            graphics2D.setColor(Color.RED);
            int inset = 3;
            graphics2D.drawOval(inset, inset, w - 2 * inset, h - 2 * inset);
        }
    }
}