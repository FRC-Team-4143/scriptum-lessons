import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Searches a field map. Each method gets the map as an adjacency list:
// waypoint name -> the waypoints one lane away.
public class PathFinder {
    // TODO: true if the robot can drive from "from" to "to" along lanes.
    //   - A waypoint can always reach itself.
    //   - A waypoint that isn't on the map can't reach, or be reached.
    //   - Keep a Set of waypoints you've already visited, or a loop of
    //     lanes (A-B-C-A) will send the search round and round forever.
    public static boolean canReach(Map<String, List<String>> field, String from, String to) {
        return false;
    }

    // TODO: the path with the fewest hops, as a list of waypoints that
    //   starts with "from" and ends with "to". Use a breadth-first search:
    //   a queue, and a map that remembers where you came from to reach
    //   each waypoint.
    //   - from equal to to: a list with just that one waypoint.
    //   - no path, or a waypoint that isn't on the map: an empty list.
    public static List<String> shortestPath(Map<String, List<String>> field, String from, String to) {
        return new ArrayList<>();
    }

    // TODO: every waypoint on the map that the robot can NOT reach from
    //   start, sorted alphabetically. Search once from start, then list
    //   the waypoints the search never visited. If start isn't on the map,
    //   nothing is reachable.
    public static List<String> unreachable(Map<String, List<String>> field, String start) {
        return new ArrayList<>();
    }
}
