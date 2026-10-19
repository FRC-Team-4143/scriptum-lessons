import java.util.HashSet;

// Every team our scouts have written down, with no repeats.
public class TeamsSeen {
    protected HashSet<Integer> teams_ = new HashSet<>();

    public void addTeam(int team) {
        teams_.add(team);
    }

    public boolean hasSeen(int team) {
        return teams_.contains(team);
    }

    public int countTeams() {
        return teams_.size();
    }
}
