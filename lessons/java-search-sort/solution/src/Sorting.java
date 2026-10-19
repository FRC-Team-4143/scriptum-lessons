import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Putting scouted teams in order for the pick list.
public class Sorting {
    // Selection sort.
    public static void sortByScore(TeamScore[] teams) {
        for (int spot = 0; spot < teams.length; spot++) {
            int highest = spot;
            for (int i = spot + 1; i < teams.length; i++) {
                if (teams[i].getScore() > teams[highest].getScore()) {
                    highest = i;
                }
            }
            TeamScore swap = teams[spot];
            teams[spot] = teams[highest];
            teams[highest] = swap;
        }
    }

    public static List<TeamScore> rankings(List<TeamScore> teams) {
        List<TeamScore> ranked = new ArrayList<>(teams);
        ranked.sort(Comparator.comparingInt(TeamScore::getScore).reversed()
            .thenComparingInt(TeamScore::getTeam));
        return ranked;
    }
}
