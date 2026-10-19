import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// The field as a graph. Each waypoint (a spot the robot can drive to) is a
// key in the map, and its value is the list of waypoints one lane away.
// That list is called its "neighbors", and the whole idea is called an
// "adjacency list".
public class FieldMap {
    protected Map<String, List<String>> neighbors_ = new LinkedHashMap<>();

    // TODO: put the waypoint on the map with an empty list of neighbors.
    //   If it is already on the map, leave it alone: don't throw away the
    //   lanes it already has.
    public void addWaypoint(String name) {
    }

    // TODO: join a and b with a lane. A lane goes both ways, so b goes in
    //   a's list AND a goes in b's list. Add either waypoint if it's new.
    //   Connecting the same two waypoints twice must not list them twice.
    public void connect(String a, String b) {
    }

    // TODO: the list of waypoints one lane away from name. For a waypoint
    //   that isn't on the map, return an empty list (never null).
    public List<String> neighbors(String name) {
        return new ArrayList<>();
    }

    public int waypointCount() {
        return neighbors_.size();
    }

    // The whole map, for PathFinder to search.
    public Map<String, List<String>> getMap() {
        return neighbors_;
    }
}
