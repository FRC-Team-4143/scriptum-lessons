public class Cleaner {
    // How many readings are real ones (0 or more). Readings below 0 are glitches.
    // BUG (Pause a stuck program): this never finishes. Pause it in the debugger to see where
    // it's stuck.
    public static int countValid(double[] readings) {
        int count = 0;
        int i = 0;
        while (i < readings.length) {
            if (readings[i] < 0) {
                continue;
            }
            count++;
            i++;
        }
        return count;
    }
}
