// One team and its scouting score. You don't need to change this file.
public class TeamScore {
    private final int team_;
    private final int score_;

    public TeamScore(int team, int score) {
        team_ = team;
        score_ = score;
    }

    public int getTeam() {
        return team_;
    }

    public int getScore() {
        return score_;
    }

    @Override
    public String toString() {
        return team_ + " (" + score_ + ")";
    }
}
