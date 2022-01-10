/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

import javafx.scene.paint.Color;

public class Edge {
    private final double X1, Y1, X2, Y2;
    private final Color color;
    
    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public double getX1() {
        return X1;
    }

    public double getX2() {
        return X2;
    }

    public double getY1() {
        return Y1;
    }

    public double getY2() {
        return Y2;
    }
}
