package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;



import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import Utils.Utils;


public class Route {
    private List<DeliveryPoint> points;

    public Route() {
        this.points = new ArrayList<>();
    }

    public void addPoint(DeliveryPoint point) {
        points.add(point);
    }

    public List<DeliveryPoint> getPoints() {
        return points;
    }

    public double calculateTotalDistance(Point depotLocation) {
        double totalDistance = 0;
        Point lastPoint = depotLocation;
        for (DeliveryPoint point : points) {
            totalDistance += Utils.calculateDistance(lastPoint, point.getLocation());
            lastPoint = point.getLocation();
        }
        totalDistance += Utils.calculateDistance(lastPoint, depotLocation);
        return totalDistance;
    }
}
