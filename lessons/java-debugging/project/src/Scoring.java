public class Scoring {
    // Points for scoring at level 1 (low), level 2 (mid) and level 3 (high).
    public static final int[] POINTS = {2, 4, 6};

    // How many points one cycle at this level is worth.
    public static int pointsFor(int level) {
        return POINTS[level];
    }

    // The points from every cycle added together.
    // BUG (checkpoint 6): this crashes the program. Read the stack trace.
    public static int totalPoints(int[] levels) {
        int total = 0;
        for (int i = 0; i < levels.length; i++) {
            total += pointsFor(levels[i]);
        }
        return total;
    }
}
