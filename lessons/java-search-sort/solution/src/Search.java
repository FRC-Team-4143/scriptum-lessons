import java.util.List;

// Finding a team in a list of team numbers.
public class Search {
    public static int indexOf(List<Integer> teams, int team) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i) == team) {
                return i;
            }
        }
        return -1;
    }

    public static int binarySearch(List<Integer> sorted, int team) {
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            int middle_team = sorted.get(middle);
            if (middle_team == team) {
                return middle;
            } else if (middle_team < team) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return -1;
    }
}
