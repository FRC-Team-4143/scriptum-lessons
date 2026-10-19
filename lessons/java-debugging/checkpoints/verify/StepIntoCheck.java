public class StepIntoCheck {
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
        System.out.println("Accuracy.percent works out real percentages.");
    }

    static void check() {
        double got = Accuracy.percent(7, 8);
        if (!near(got, 87.5)) {
            fail("Accuracy.percent(7, 8) was " + got + ", expected 87.5. Step into it and look at what made / attempted gives.");
        }
        got = Accuracy.percent(1, 3);
        if (Math.abs(got - 33.3333) > 0.001) {
            fail("Accuracy.percent(1, 3) was " + got + ", expected about 33.33.");
        }
        got = Accuracy.percent(5, 5);
        if (!near(got, 100.0)) {
            fail("Accuracy.percent(5, 5) was " + got + ", expected 100.0.");
        }
        got = Accuracy.percent(0, 0);
        if (!near(got, 0.0)) {
            fail("Accuracy.percent(0, 0) was " + got + ", expected 0.0 (no attempts counts as 0).");
        }
    }
}
