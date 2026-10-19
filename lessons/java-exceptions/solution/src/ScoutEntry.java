// One line of scouting data: which team played which match, and how many
// points it scored.
public class ScoutEntry {
    protected int team_;
    protected int match_;
    protected int points_;

    public ScoutEntry(int team, int match, int points) {
        if (team <= 0) {
            throw new IllegalArgumentException("team must be positive, got " + team);
        }
        if (match <= 0) {
            throw new IllegalArgumentException("match must be positive, got " + match);
        }
        if (points < 0) {
            throw new IllegalArgumentException("points can't be negative, got " + points);
        }
        team_ = team;
        match_ = match;
        points_ = points;
    }

    public int getTeam() {
        return team_;
    }

    public int getMatch() {
        return match_;
    }

    public int getPoints() {
        return points_;
    }

    @Override
    public String toString() {
        return "Team " + team_ + ", match " + match_ + ": " + points_ + " points";
    }
}
