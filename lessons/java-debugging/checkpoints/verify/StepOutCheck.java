public class StepOutCheck {
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
        System.out.println("totalDistance adds up every leg.");
    }

    static void check() {
        double got = Odometry.totalDistance(new double[] {1.0, 2.0, 3.5});
        if (!near(got, 6.5)) {
            fail("totalDistance of {1.0, 2.0, 3.5} was " + got + ", expected 6.5. Step into addLeg, then step out: is the total in totalDistance any bigger?");
        }
        got = Odometry.totalDistance(new double[] {4.25});
        if (!near(got, 4.25)) {
            fail("totalDistance of {4.25} was " + got + ", expected 4.25.");
        }
        got = Odometry.totalDistance(new double[0]);
        if (!near(got, 0.0)) {
            fail("totalDistance of an empty array was " + got + ", expected 0.0.");
        }
    }
}
