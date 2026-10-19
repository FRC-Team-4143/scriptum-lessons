import java.util.HashSet;
import java.util.List;

// We scouted two events. Which teams did we see at both?
public class EventOverlap {
    public static int teamsInBoth(List<Integer> event1, List<Integer> event2) {
        HashSet<Integer> at_first = new HashSet<>(event1);
        HashSet<Integer> in_both = new HashSet<>();
        for (int team : event2) {
            if (at_first.contains(team)) {
                in_both.add(team);
            }
        }
        return in_both.size();
    }
}
