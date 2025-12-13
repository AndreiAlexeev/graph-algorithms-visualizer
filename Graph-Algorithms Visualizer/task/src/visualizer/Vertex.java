package visualizer;

import javax.swing.*;
import java.awt.*;

public class Vertex extends JPanel {
    private static final int DEFAULT_VERTEX_SIZE = 50;
    int xVertex;
    int yVertex;
    boolean isVertexSelected;

    public Vertex(int x, int y, String strValue) {
        this.xVertex = x - DEFAULT_VERTEX_SIZE / 2;
        this.yVertex = y - DEFAULT_VERTEX_SIZE / 2;

        setName("Vertex " + strValue);
        setBounds(xVertex, yVertex, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE);
        setPreferredSize(new Dimension(DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE));
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

    }

    public static int getDefaultVertexSize() {
        return DEFAULT_VERTEX_SIZE;
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