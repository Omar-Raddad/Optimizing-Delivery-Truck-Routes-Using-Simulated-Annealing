package algorithm;



import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.DeliveryPoint;
import model.Route;

public class SimulatedAnnealing {
    private static Random random = new Random();

    public static Route optimizeRoute(Route route, Point depotLocation) {
        double temperature = 1000;
        double coolingRate = 0.003;

        List<DeliveryPoint> currentSolution = new ArrayList<>(route.getPoints());
        List<DeliveryPoint> bestSolution = new ArrayList<>(currentSolution);
        double bestDistance = route.calculateTotalDistance(depotLocation);

        while (temperature > 1) {
            List<DeliveryPoint> newSolution = new ArrayList<>(currentSolution);
            int swapIndex1 = random.nextInt(newSolution.size());
            int swapIndex2 = random.nextInt(newSolution.size());

            Collections.swap(newSolution, swapIndex1, swapIndex2);

            Route newRoute = new Route();
            for (DeliveryPoint point : newSolution) {
                newRoute.addPoint(point);
            }
            double newDistance = newRoute.calculateTotalDistance(depotLocation);

            if (acceptanceProbability(bestDistance, newDistance, temperature) > Math.random()) {
                currentSolution = new ArrayList<>(newSolution);
            }

            if (newDistance < bestDistance) {
                bestSolution = new ArrayList<>(currentSolution);
                bestDistance = newRoute.calculateTotalDistance(depotLocation);
            }

            temperature *= 1 - coolingRate;
        }

        Route optimizedRoute = new Route();
        for (DeliveryPoint point : bestSolution) {
            optimizedRoute.addPoint(point);
        }

        return optimizedRoute;
    }

    private static double acceptanceProbability(double currentDistance, double newDistance, double temperature) {
        if (newDistance < currentDistance) {
            return 1.0;
        }
        return Math.exp((currentDistance - newDistance) / temperature);
    }
}
