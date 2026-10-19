import java.util.HashMap;

// Total points scored by each team: team number -> points.
public class TeamTotals {
    protected HashMap<Integer, Integer> totals_ = new HashMap<>();

    public void addMatch(int team, int points) {
        totals_.put(team, totalFor(team) + points);
    }

    public int totalFor(int team) {
        return totals_.getOrDefault(team, 0);
    }

    public int topTeam() {
        int top_team = -1;
        int top_total = 0;
        for (int team : totals_.keySet()) {
            int total = totals_.get(team);
            boolean better = total > top_total;
            boolean tie_with_lower_number = total == top_total && team < top_team;
            if (top_team == -1 || better || tie_with_lower_number) {
                top_team = team;
                top_total = total;
            }
        }
        return top_team;
    }
}
