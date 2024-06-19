package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainUi extends JFrame {
    // Define UI components and data structures
    private JPanel mapPanel;
    private JButton addButton, initializeRoutesButton, optimizeRoutesButton, clearButton;
    private JTextField weightField;
    private List<JLabel> beforeOptimizationLabels;
    private List<JLabel> afterOptimizationLabels;
    private ArrayList<Point> deliveryPoints;
    private ArrayList<Integer> weights;
    private Point depot = new Point(50, 50); // Fixed depot location
    private static final int TRUCK_CAPACITY = 100; // Fixed truck capacity
    private int numberOfTrucks = 5; // Initial number of trucks
    private List<List<Point>> routes; // Store routes
    private Random random = new Random();

    public MainUi() {
        // Initialize the frame
        setTitle("VRP Optimization using Simulated Annealing");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the delivery points and weights lists
        deliveryPoints = new ArrayList<>();
        weights = new ArrayList<>();
        routes = new ArrayList<>();

        // Set up the map panel for drawing points and routes
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                for (int i = 0; i < deliveryPoints.size(); i++) {
                    Point point = deliveryPoints.get(i);
                    g.fillOval(point.x - 3, point.y - 3, 6, 6); // Draw delivery points
                    g.drawString(String.valueOf(weights.get(i)), point.x + 5, point.y - 5); // Draw weights
                }
                // Draw depot
                g.setColor(Color.BLUE);
                g.fillRect(depot.x - 5, depot.y - 5, 10, 10);
                g.drawString("Depot", depot.x + 10, depot.y);

                // Draw routes and distances
                Color[] colors = {Color.BLACK, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK};
                for (int i = 0; i < routes.size(); i++) {
                    List<Point> route = routes.get(i);
                    g.setColor(colors[i % colors.length]);
                    Point lastPoint = depot;
                    for (Point point : route) {
                        g.drawLine(lastPoint.x, lastPoint.y, point.x, point.y); // Draw lines between points
                        // Calculate and draw the distance
                        double distance = calculateDistance(lastPoint, point);
                        int midX = (lastPoint.x + point.x) / 2;
                        int midY = (lastPoint.y + point.y) / 2;
                        g.drawString(String.format("%.1f", distance), midX, midY); // Draw distance
                        lastPoint = point;
                    }
                    g.drawLine(lastPoint.x, lastPoint.y, depot.x, depot.y); // Return to depot
                    double distance = calculateDistance(lastPoint, depot);
                    int midX = (lastPoint.x + depot.x) / 2;
                    int midY = (lastPoint.y + depot.y) / 2;
                    g.drawString(String.format("%.1f", distance), midX, midY); // Draw distance to depot
                }
            }
        };
        mapPanel.setBackground(Color.WHITE);
        mapPanel.setPreferredSize(new Dimension(600, 600));
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addDeliveryPoint(e.getPoint()); // Add delivery point on mouse click
            }
        });

        // Set up the control panel with buttons and input fields
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        addButton = new JButton("Add Point");
        addButton.addActionListener(e -> addPoint()); // Simulate adding a point

        weightField = new JTextField();
        weightField.setMaximumSize(new Dimension(Integer.MAX_VALUE, weightField.getPreferredSize().height));

        initializeRoutesButton = new JButton("Initialize Routes");
        initializeRoutesButton.addActionListener(e -> initializeRoutes()); // Initialize routes

        optimizeRoutesButton = new JButton("Optimize Routes");
        optimizeRoutesButton.addActionListener(e -> optimizeRoutes()); // Optimize routes

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearMap()); // Clear the map

        beforeOptimizationLabels = new ArrayList<>();
        afterOptimizationLabels = new ArrayList<>();

        controlPanel.add(new JLabel("Goods Weight"));
        controlPanel.add(weightField);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(addButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(initializeRoutesButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(optimizeRoutesButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(clearButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (int i = 0; i < numberOfTrucks; i++) {
            JLabel beforeLabel = new JLabel("Truck " + (i + 1) + " Distance Before: 0");
            JLabel afterLabel = new JLabel("Truck " + (i + 1) + " Distance After: 0");
            beforeOptimizationLabels.add(beforeLabel);
            afterOptimizationLabels.add(afterLabel);
            controlPanel.add(beforeLabel);
            controlPanel.add(afterLabel);
            controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Add panels to the frame
        add(mapPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }

    // Method to add a delivery point at a given location
    private void addDeliveryPoint(Point point) {
        try {
            int weight = Integer.parseInt(weightField.getText()); // Parse weight from input
            if (weight <= 0) {
                throw new NumberFormatException();
            }
            deliveryPoints.add(point);
            weights.add(weight);
            mapPanel.repaint(); // Repaint map to show new point
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid weight.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Simulate clicking on the map panel at a random location (for testing purposes)
    private void addPoint() {
        Point randomPoint = new Point((int) (Math.random() * 600), (int) (Math.random() * 600));
        addDeliveryPoint(randomPoint);
    }

    // Initialize routes based on delivery points and weights
    private void initializeRoutes() {
        // Shuffle delivery points to ensure random distribution each time
        List<Integer> shuffledIndices = new ArrayList<>();
        for (int i = 0; i < deliveryPoints.size(); i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices);

        // Initialize routes based on delivery points and weights
        routes.clear();
        for (int i = 0; i < numberOfTrucks; i++) {
            routes.add(new ArrayList<>());
        }

        int[] truckLoads = new int[numberOfTrucks];

        for (int index : shuffledIndices) {
            Point point = deliveryPoints.get(index);
            int weight = weights.get(index);
            boolean assigned = false;

            for (int j = 0; j < numberOfTrucks; j++) {
                if (truckLoads[j] + weight <= TRUCK_CAPACITY) {
                    routes.get(j).add(point);
                    truckLoads[j] += weight;
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                JOptionPane.showMessageDialog(this, "Not enough truck capacity to assign all points.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Calculate and display the total distance before optimization for each truck
        for (int i = 0; i < numberOfTrucks; i++) {
            double totalDistance = calculateTotalDistance(routes.get(i));
            beforeOptimizationLabels.get(i).setText(String.format("Truck %d Distance Before: %.2f", i + 1, totalDistance));
        }

        // Redraw map with routes
        mapPanel.repaint();
    }

    // Optimize routes using simulated annealing
    private void optimizeRoutes() {
        int itermax = 1000;  // Maximum number of iterations
        double T = 1000;     // Initial temperature

        for (int i = 0; i < numberOfTrucks; i++) {
            List<Point> route = routes.get(i);
            if (route.size() > 1) { // Ensure there are at least two points to optimize
                List<Point> optimizedRoute = simulateAnneal(route, itermax, T);
                routes.set(i, optimizedRoute);
            }
            double totalDistance = calculateTotalDistance(routes.get(i));
            afterOptimizationLabels.get(i).setText(String.format("Truck %d Distance After: %.2f", i + 1, totalDistance));
        }

        // Redraw map with optimized routes
        mapPanel.repaint();
    }

    // Simulated annealing algorithm to optimize the route
    private List<Point> simulateAnneal(List<Point> route, int itermax, double T) {
        List<Point> xcurr = new ArrayList<>(route);  // Initial configuration
        List<Point> xbest = new ArrayList<>(xcurr);  // Best configuration
        double bestDistance = calculateTotalDistance(xbest);

        for (int i = 1; i <= itermax; i++) {
            double Tc = calcTemp(i, T); // Calculate temperature
            List<Point> xnext = randomSuccessor(xcurr); // Generate a new state
            double AE = calculateTotalDistance(xnext) - calculateTotalDistance(xcurr);

            if (AE < 0) {  // If the new solution is better
                xcurr = new ArrayList<>(xnext);
                if (calculateTotalDistance(xcurr) < bestDistance) {
                    xbest = new ArrayList<>(xcurr);
                    bestDistance = calculateTotalDistance(xbest);
                }
            } else if (Math.exp(-AE / Tc) > random.nextDouble()) {  // Accept worse solution with a probability
                xcurr = new ArrayList<>(xnext);
            }
        }

        return xbest;
    }

    // A simple cooling schedule: decrease temperature linearly
    private double calcTemp(int iteration, double initialTemp) {
        return initialTemp * (1 - (double) iteration / 1000);
    }

    // Generate a random successor by swapping two points
    private List<Point> randomSuccessor(List<Point> route) {
        List<Point> newRoute = new ArrayList<>(route);
        int swapIndex1 = random.nextInt(newRoute.size());
        int swapIndex2 = random.nextInt(newRoute.size());
        Collections.swap(newRoute, swapIndex1, swapIndex2);
        return newRoute;
    }

    // Calculate the acceptance probability for a worse solution
    private double acceptanceProbability(double currentDistance, double newDistance, double temperature) {
        if (newDistance < currentDistance) {
            return 1.0;
        }
        return Math.exp((currentDistance - newDistance) / temperature);
    }

    // Calculate the total distance of a route
    private double calculateTotalDistance(List<Point> route) {
        double totalDistance = 0;
        Point lastPoint = depot;
        for (Point point : route) {
            totalDistance += calculateDistance(lastPoint, point);
            lastPoint = point;
        }
        totalDistance += calculateDistance(lastPoint, depot); // Return to depot
        return totalDistance;
    }

    // Clear the map and reset everything
    private void clearMap() {
        deliveryPoints.clear();
        weights.clear();
        routes.clear();
        for (JLabel label : beforeOptimizationLabels) {
            label.setText("Truck Distance Before: 0");
        }
        for (JLabel label : afterOptimizationLabels) {
            label.setText("Truck Distance After: 0");
        }
        mapPanel.repaint();
    }

    // Calculate the distance between two points
    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUi gui = new MainUi();
            gui.setVisible(true);
        });
    }
}
