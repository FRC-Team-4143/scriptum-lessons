import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Searches a field map. Each method gets the map as an adjacency list:
// waypoint name -> the waypoints one lane away.
public class PathFinder {
    public static boolean canReach(Map<String, List<String>> field, String from, String to) {
        if (!field.containsKey(from) || !field.containsKey(to)) {
            return false;
        }
        return visitedFrom(field, from).contains(to);
    }

    public static List<String> shortestPath(Map<String, List<String>> field, String from, String to) {
        List<String> path = new ArrayList<>();
        if (!field.containsKey(from) || !field.containsKey(to)) {
            return path;
        }
        Map<String, String> came_from = new HashMap<>();
        Deque<String> queue = new ArrayDeque<>();
        came_from.put(from, from);
        queue.addLast(from);
        while (!queue.isEmpty()) {
            String here = queue.removeFirst();
            if (here.equals(to)) {
                break;
            }
            for (String next : field.getOrDefault(here, new ArrayList<>())) {
                if (!came_from.containsKey(next)) {
                    came_from.put(next, here);
                    queue.addLast(next);
                }
            }
        }
        if (!came_from.containsKey(to)) {
            return path;
        }
        String step = to;
        path.add(step);
        while (!step.equals(from)) {
            step = came_from.get(step);
            path.add(0, step);
        }
        return path;
    }

    public static List<String> unreachable(Map<String, List<String>> field, String start) {
        Set<String> reached = new HashSet<>();
        if (field.containsKey(start)) {
            reached = visitedFrom(field, start);
        }
        List<String> cut_off = new ArrayList<>();
        for (String waypoint : field.keySet()) {
            if (!reached.contains(waypoint)) {
                cut_off.add(waypoint);
            }
        }
        Collections.sort(cut_off);
        return cut_off;
    }

    // A depth-first search: every waypoint the robot can get to from start.
    private static Set<String> visitedFrom(Map<String, List<String>> field, String start) {
        Set<String> visited = new HashSet<>();
        Deque<String> to_visit = new ArrayDeque<>();
        to_visit.push(start);
        while (!to_visit.isEmpty()) {
            String here = to_visit.pop();
            if (visited.contains(here)) {
                continue;
            }
            visited.add(here);
            for (String next : field.getOrDefault(here, new ArrayList<>())) {
                if (!visited.contains(next)) {
                    to_visit.push(next);
                }
            }
        }
        return visited;
    }
}
