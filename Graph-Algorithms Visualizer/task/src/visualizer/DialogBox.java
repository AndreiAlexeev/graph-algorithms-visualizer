package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogBox {
    private DialogBox() {
        throw new IllegalStateException("Utility class");
    }

    public static String askVertexName(Component component) {
        while (true) {
            String nameNewVertex = JOptionPane
                    .showInputDialog(
                            component,
                            "Enter the Vertex ID (Should be 1 char): ",
                            "Vertex", JOptionPane.PLAIN_MESSAGE
                    );
            if (nameNewVertex == null) {
                return null;
            }

            nameNewVertex = nameNewVertex.trim();

            if (nameNewVertex.length() == 1) {
                return nameNewVertex;
            }
            //JOptionPane.showMessageDialog(component, "Enter only 1 character!");
        }
//        return null;
    }

    public static String askEdgeWeight(Component component) {
        while (true) {//posibil sa sterg acest while
            String nameNewEdge = JOptionPane
                    .showInputDialog(
                            component,
                            "Enter Weight: ",
                            "Input",
                            JOptionPane.PLAIN_MESSAGE
                    );
            if (nameNewEdge == null) {
                return null;
            }

            nameNewEdge = nameNewEdge.trim();

            if (nameNewEdge.isEmpty()){
                continue;
            }

            try {
                int weight = Integer.parseInt(nameNewEdge);
                return nameNewEdge;

//                if (weight > 0) {
//                    return nameNewEdge;
//                } else {
//                    DialogBox.showMessage(component, "The number should positive!");
//                    return null;
//                }
            } catch (NumberFormatException numberFormatException) {
//                DialogBox.showMessage(component, "You should introduce a number!");
//                return null;
            }
        }
    }

    // short message in case of failure
    public static void showMessage(Component component, String text) {
        JLabel label = new JLabel();
        label.setText(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        label.setForeground(new Color(0, 102, 51));
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(component),
                Dialog.ModalityType.MODELESS
        );
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getContentPane().add(label, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(component);


        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
    }

    public static boolean confirmVertexDeletion(Component component, String vertexName) {

        int choice = JOptionPane.showConfirmDialog(
                component,
                "Do you want to  delete vertex \"" + vertexName + "\"",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        return choice == JOptionPane.YES_OPTION;
    }

    //metodele de confirmare ar trebui sa le optimizez intr-o metoda singura
    public static boolean confirmEdgeDeletion(Component component, String edgeName) {

        int choice = JOptionPane.showConfirmDialog(
                component,
                "Do you want to delete edge \"" + edgeName + "\"",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }
}