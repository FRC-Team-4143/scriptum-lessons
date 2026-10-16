public class FixTheAverageCheck {
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
        System.out.println("averageSpeed counts every speed now.");
    }

    static void check() {
        double got = SpeedMath.averageSpeed(new double[] {1.0, 2.0, 3.0, 4.0});
        if (!near(got, 2.5)) {
            fail("averageSpeed of {1.0, 2.0, 3.0, 4.0} was " + got + ", expected 2.5. Is every speed added to sum?");
        }
        got = SpeedMath.averageSpeed(new double[] {5.0});
        if (!near(got, 5.0)) {
            fail("averageSpeed of {5.0} was " + got + ", expected 5.0. Is every speed added to sum?");
        }
        got = SpeedMath.averageSpeed(new double[] {-2.0, 2.0, 6.0});
        if (!near(got, 2.0)) {
            fail("averageSpeed of {-2.0, 2.0, 6.0} was " + got + ", expected 2.0.");
        }
    }
}
