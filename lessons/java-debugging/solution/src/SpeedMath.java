public class SpeedMath {
    // The average of all the speeds.
    public static double averageSpeed(double[] speeds) {
        double sum = 0.0;
        for (int i = 0; i < speeds.length; i++) {
            sum += speeds[i];
        }
        return sum / speeds.length;
    }
}
