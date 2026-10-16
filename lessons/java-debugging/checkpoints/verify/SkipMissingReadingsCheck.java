public class SkipMissingReadingsCheck {
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
        System.out.println("lowestVolts skips the missed readings.");
    }

    static void check() {
        double got = Battery.lowestVolts(new int[] {12400, 11800, 12100});
        if (!near(got, 11.8)) {
            fail("lowestVolts of {12400, 11800, 12100} was " + got + ", expected 11.8.");
        }
        got = Battery.lowestVolts(new int[] {12000, -1, 11500});
        if (!near(got, 11.5)) {
            fail("lowestVolts of {12000, -1, 11500} was " + got + ", expected 11.5. A -1 is a missed reading, so skip it.");
        }
        got = Battery.lowestVolts(new int[] {-1, 12300, 11900});
        if (!near(got, 11.9)) {
            fail("lowestVolts of {-1, 12300, 11900} was " + got + ", expected 11.9. What happens when the very first reading is the missed one?");
        }
        got = Battery.lowestVolts(new int[] {-1, -1});
        if (!near(got, 0.0)) {
            fail("lowestVolts of {-1, -1} was " + got + ", expected 0.0 (no real readings at all).");
        }
        got = Battery.lowestVolts(new int[0]);
        if (!near(got, 0.0)) {
            fail("lowestVolts of an empty array was " + got + ", expected 0.0.");
        }
    }
}
