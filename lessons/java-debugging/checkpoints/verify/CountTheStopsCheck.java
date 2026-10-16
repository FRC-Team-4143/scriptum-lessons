public class CountTheStopsCheck {
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
        System.out.println("That's how many times the old loop ran: one short of the 8 speeds.");
    }

    static void check() {
        if (Answers.LOOP_RUNS_BEFORE_FIX != MatchLog.SPEEDS.length - 1) {
            fail("Answers.LOOP_RUNS_BEFORE_FIX is " + Answers.LOOP_RUNS_BEFORE_FIX
                + ". Count how many times the breakpoint inside averageSpeed's loop stops, with the loop as it was before your fix"
                + ".");
        }
    }
}
