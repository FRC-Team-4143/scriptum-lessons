public class Odometry {
    // Adds one leg to the running total, and hands back the new total.
    public static double addLeg(double total, double leg) {
        return total + leg;
    }

    // How far the robot drove in all of its legs together.
    public static double totalDistance(double[] legs) {
        double total = 0.0;
        for (int i = 0; i < legs.length; i++) {
            total = addLeg(total, legs[i]);
        }
        return total;
    }
}
