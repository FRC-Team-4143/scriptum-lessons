import java.util.List;

// Finding a team in a list of team numbers.
public class Search {
    // TODO: linear search. Return the index of the FIRST place `team`
    //   appears in `teams`, or -1 if it isn't there.
    //   Check the items one at a time from the front, and stop as soon as
    //   you find it. The list can be in any order.
    public static int indexOf(List<Integer> teams, int team) {
        return -1;
    }

    // TODO: binary search. `sorted` holds different team numbers, smallest
    //   first. Return the index of `team`, or -1 if it isn't there.
    //   Keep two indexes, low and high, for the part of the list the team
    //   could still be in. Look at the middle item: if it's too small, the
    //   team can only be to its right; if it's too big, only to its left.
    //   Each look throws away half, so 1000 teams take about 10 looks.
    //   Write the loop yourself: don't call Collections.binarySearch.
    public static int binarySearch(List<Integer> sorted, int team) {
        return -1;
    }
}
