/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import javafx.scene.paint.Color;

import java.util.Observable;

/**
 * @author Peter Boots
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochFractalGenerator extends Observable {
    private DrawEdgeTask drawEdgeTask;
    private int level = 1;      // The current level of the fractal
    private int numberOfEdges = 3;  // The number of edges in the current level of the fractal
    private float hue;          // Hue value of color for next edge
    private boolean cancelled;  // Flag to indicate that calculation has been cancelled

    public KochFractalGenerator(DrawEdgeTask drawEdgeTask) {
        this.drawEdgeTask = drawEdgeTask;
    }

    private void drawKochEdge(double ax, double ay, double bx, double by, int n) {
        if (!cancelled) {
            if (n == 1) {
                hue = hue + 1.0f / numberOfEdges;
                Edge edge = new Edge(ax, ay, bx, by, Color.hsb(hue * 360.0, 1.0, 1.0));
                setChanged();
                notifyObservers(edge);

            } else {
                double angle = Math.PI / 3.0 + Math.atan2(by - ay, bx - ax);
                double distabdiv3 = Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay)) / 3;
                double cx = Math.cos(angle) * distabdiv3 + (bx - ax) / 3 + ax;
                double cy = Math.sin(angle) * distabdiv3 + (by - ay) / 3 + ay;
                final double midabx = (bx - ax) / 3 + ax;
                final double midaby = (by - ay) / 3 + ay;
                drawKochEdge(ax, ay, midabx, midaby, n - 1);
                drawKochEdge(midabx, midaby, cx, cy, n - 1);
                drawKochEdge(cx, cy, (midabx + bx) / 2, (midaby + by) / 2, n - 1);
                drawKochEdge((midabx + bx) / 2, (midaby + by) / 2, bx, by, n - 1);
            }
        }
    }

    public void leftEdge() {
        hue = 0f;
        cancelled = false;
        drawKochEdge(0.5, 0.0, (1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, level);
    }

    public void bottomEdge() {
        hue = 1f / 3f;
        cancelled = false;
        drawKochEdge((1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, (1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, level);
    }

    public void rightEdge() {
        hue = 2f / 3f;
        cancelled = false;
        drawKochEdge((1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, 0.5, 0.0, level);
    }

    public void cancel() {
        cancelled = true;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        numberOfEdges = (int) (3 * Math.pow(4, this.level - 1));
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }
}
