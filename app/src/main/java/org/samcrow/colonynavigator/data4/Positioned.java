package org.samcrow.colonynavigator.data4;

/**
 * A base class for something with a position on the site
 */
public class Positioned {
    /** The X-coordinate in meters east of the southwest corner */
    private double x;
    /** The Y-coordinate in meters north of the southwest corner */
    private double y;

    public Positioned(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
