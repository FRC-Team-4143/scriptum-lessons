import java.util.List;

// Putting scouted teams in order for the pick list.
public class Sorting {
    // TODO: sort `teams` in place, highest score first, with loops you write
    //   yourself: selection sort or insertion sort.
    //   Selection sort: for each spot from the front, find the highest score
    //   in the rest of the array and swap it into that spot.
    //   Teams with the same score can end up in either order.
    //   Don't call Arrays.sort or List.sort here: that's the next job.
    public static void sortByScore(TeamScore[] teams) {
    }

    // TODO: return a NEW list with the same teams, ranked highest score
    //   first. When two teams have the same score, the lower team number
    //   goes first. Use List.sort with a Comparator.
    //   Don't change the list you were given: copy it first.
    public static List<TeamScore> rankings(List<TeamScore> teams) {
        return teams;
    }
}
