public class PauseAHangCheck {
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
        System.out.println("countValid finishes, and counts the real readings.");
    }

    static void check() {
        int got = Cleaner.countValid(new double[] {0.5, -1.0, 0.7, -1.0, -1.0, 0.0});
        if (got != 3) {
            fail("countValid of {0.5, -1.0, 0.7, -1.0, -1.0, 0.0} was " + got + ", expected 3 (0.0 is a real reading).");
        }
        got = Cleaner.countValid(new double[] {2.0, 1.5});
        if (got != 2) {
            fail("countValid of {2.0, 1.5} was " + got + ", expected 2.");
        }
        got = Cleaner.countValid(new double[] {-1.0});
        if (got != 0) {
            fail("countValid of {-1.0} was " + got + ", expected 0.");
        }
    }
}
