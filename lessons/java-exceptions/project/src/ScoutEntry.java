// One line of scouting data: which team played which match, and how many
// points it scored.
public class ScoutEntry {
    protected int team_;
    protected int match_;
    protected int points_;

    // TODO: refuse bad numbers before storing them. Throw an
    //   IllegalArgumentException (with a message that says what was wrong) when
    //   - team is 0 or less,
    //   - match is 0 or less, or
    //   - points is less than 0 (0 points is fine: it was a bad match).
    public ScoutEntry(int team, int match, int points) {
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
