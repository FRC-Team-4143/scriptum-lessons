import java.util.ArrayList;

// One team's match scores, in the order they were played.
public class ScoreList {
    protected ArrayList<Integer> scores_ = new ArrayList<>();

    public void addScore(int score) {
        scores_.add(score);
    }

    public int count() {
        return scores_.size();
    }

    public double average() {
        if (scores_.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (int score : scores_) {
            total += score;
        }
        return (double) total / scores_.size();
    }

    public int best() {
        if (scores_.isEmpty()) {
            return 0;
        }
        int best = scores_.get(0);
        for (int score : scores_) {
            if (score > best) {
                best = score;
            }
        }
        return best;
    }
}
