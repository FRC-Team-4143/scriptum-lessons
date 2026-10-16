public class SpeedMath {
    // The average of all the speeds.
    // BUG (Fix the average): the answer is too low. Find out why with a breakpoint.
    public static double averageSpeed(double[] speeds) {
        double sum = 0.0;
        for (int i = 0; i < speeds.length - 1; i++) {
            sum += speeds[i];
        }
        return sum / speeds.length;
    }
}
