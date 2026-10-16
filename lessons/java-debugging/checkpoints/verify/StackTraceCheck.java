public class StackTraceCheck {
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
        System.out.println("Scoring no longer crashes, and every level is worth the right points.");
    }

    static void check() {
        int[] want = {2, 4, 6};
        for (int level = 3; level >= 1; level--) {
            int got;
            try {
                got = Scoring.pointsFor(level);
            } catch (RuntimeException e) {
                fail("pointsFor(" + level + ") crashed with " + e + ". Read the stack trace: which line, and which index?");
                return;
            }
            if (got != want[level - 1]) {
                fail("pointsFor(" + level + ") was " + got + ", expected " + want[level - 1]
                    + ". Level 1 is low (2 points), 2 is mid (4) and 3 is high (6).");
            }
        }
        int total = Scoring.totalPoints(new int[] {3, 3, 1});
        if (total != 14) {
            fail("totalPoints of levels {3, 3, 1} was " + total + ", expected 14.");
        }
    }
}
