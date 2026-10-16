public class Scoring {
    // Points for scoring at level 1 (low), level 2 (mid) and level 3 (high).
    public static final int[] POINTS = {2, 4, 6};

    // How many points one cycle at this level is worth. Level 1 is POINTS[0].
    public static int pointsFor(int level) {
        return POINTS[level - 1];
    }

    // The points from every cycle added together.
    public static int totalPoints(int[] levels) {
        int total = 0;
        for (int i = 0; i < levels.length; i++) {
            total += pointsFor(levels[i]);
        }
        return total;
    }
}
