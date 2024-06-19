package model;

import java.util.ArrayList;
import java.util.List;

public class Truck {
    private int id;
    private int capacity;
    private List<DeliveryPoint> route;

    public Truck(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.route = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<DeliveryPoint> getRoute() {
        return route;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setRoute(List<DeliveryPoint> route) {
        this.route = route;
    }

// Methods to add delivery points, calculate load, etc.
}
