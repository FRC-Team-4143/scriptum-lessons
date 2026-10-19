import java.util.List;

// Quick numbers from one team's match scores, oldest match first.
public class ScoutStats {
    public static int latest(List<Integer> scores) {
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(scores.size() - 1);
    }

    public static int best(List<Integer> scores) {
        int best = 0;
        for (int score : scores) {
            if (score > best) {
                best = score;
            }
        }
        return best;
    }
}
