package model;

import java.awt.Point;

public class DeliveryPoint {
    private Point location;
    private int weight;

    public DeliveryPoint(Point location, int weight) {
        this.location = location;
        this.weight = weight;
    }

    public Point getLocation() {
        return location;
    }

    public int getWeight() {
        return weight;
    }
}
