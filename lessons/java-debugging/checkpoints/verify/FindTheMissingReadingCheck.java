public class FindTheMissingReadingCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) < 0.0001;
    }

    public static void main(String[] args) {
        try {
            check();
        } catch (RuntimeException e) {
            fail("Your code crashed: " + e + ". Debug it to see which line throws.");
        }
        System.out.println("Found it: that's the reading the sensor missed.");
    }

    static void check() {
        int[] log = MatchLog.batteryMillivolts();
        int index = Answers.MISSING_SAMPLE_INDEX;
        if (index < 0 || index >= log.length || log[index] != -1) {
            fail("Answers.MISSING_SAMPLE_INDEX is " + index + ", but that isn't where the -1 is."
                + " Put a breakpoint inside lowestVolts' loop, make it conditional (millivolts[i] < 0), and read i when it stops.");
        }
    }
}
