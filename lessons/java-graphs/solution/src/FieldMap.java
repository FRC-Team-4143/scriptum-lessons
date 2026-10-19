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

    public void addWaypoint(String name) {
        if (!neighbors_.containsKey(name)) {
            neighbors_.put(name, new ArrayList<>());
        }
    }

    public void connect(String a, String b) {
        addWaypoint(a);
        addWaypoint(b);
        if (!neighbors_.get(a).contains(b)) {
            neighbors_.get(a).add(b);
        }
        if (!neighbors_.get(b).contains(a)) {
            neighbors_.get(b).add(a);
        }
    }

    public List<String> neighbors(String name) {
        if (!neighbors_.containsKey(name)) {
            return new ArrayList<>();
        }
        return neighbors_.get(name);
    }

    public int waypointCount() {
        return neighbors_.size();
    }

    // The whole map, for PathFinder to search.
    public Map<String, List<String>> getMap() {
        return neighbors_;
    }
}
