package visualizer;

import java.awt.*;

public class GraphicsUtils {
    private GraphicsUtils() { //to prevent instantiation
    }

    static void doDrawing(Graphics2D graphics2D) {

        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        renderingHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        graphics2D.setRenderingHints(renderingHints);
    }
}