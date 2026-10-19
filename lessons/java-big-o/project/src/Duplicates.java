import java.util.List;
// TODO: import java.util.HashSet; up here.

// Did a scout type the same team into the pick list twice?
public class Duplicates {
    // This works, but it is slow. For every team it looks at every team
    // after it, so n teams take about n * n / 2 comparisons: O(n²).
    // Run Main to see how many looks it takes on 2000 teams.
    // (.equals, not ==, because these are Integer objects.)
    public static boolean hasDuplicateSlow(List<Integer> teams) {
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                if (teams.get(i).equals(teams.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    // TODO: give the same answer as hasDuplicateSlow, but look at each team
    //   in the list only once: O(n).
    //   Keep a HashSet of the teams you have already seen. Asking a HashSet
    //   "do you have this one?" takes about one step, however big it is.
    public static boolean hasDuplicate(List<Integer> teams) {
        return false;
    }
}
