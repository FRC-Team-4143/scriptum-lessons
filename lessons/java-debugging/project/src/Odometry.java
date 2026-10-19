public class Odometry {
    // Adds one leg to the running total.
    public static void addLeg(double total, double leg) {
        total = total + leg;
    }

    // How far the robot drove in all of its legs together.
    // BUG (Step out again): it always says 0.0. Step into addLeg, then step out again,
    // and watch what happens to total.
    public static double totalDistance(double[] legs) {
        double total = 0.0;
        for (int i = 0; i < legs.length; i++) {
            addLeg(total, legs[i]);
        }
        return total;
    }
}
