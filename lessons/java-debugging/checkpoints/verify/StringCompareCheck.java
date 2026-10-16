public class StringCompareCheck {
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
        System.out.println("countState compares what the strings say.");
    }

    static void check() {
        // Strings built while the program runs, like the ones in the match log.
        String[] states = {
            "drive".toUpperCase(), "shoot".toUpperCase(), "drive".toUpperCase(), "climb".toUpperCase(),
        };
        int got = StateLog.countState(states, "DRIVE");
        if (got != 2) {
            fail("countState found " + got + " \"DRIVE\" entries in {DRIVE, SHOOT, DRIVE, CLIMB}, expected 2."
                + " Watch states[i] == wanted in the debugger: why is it false?");
        }
        got = StateLog.countState(states, "climb".toUpperCase());
        if (got != 1) {
            fail("countState found " + got + " \"CLIMB\" entries, expected 1.");
        }
        got = StateLog.countState(states, "INTAKE");
        if (got != 0) {
            fail("countState found " + got + " \"INTAKE\" entries in a log with none, expected 0.");
        }
    }
}
