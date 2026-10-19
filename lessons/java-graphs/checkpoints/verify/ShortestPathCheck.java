import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ShortestPathCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    // Builds an adjacency list from lanes written "A-B". Lone waypoints are written without a dash.
    static Map<String, List<String>> field(String... lanes) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (String lane : lanes) {
            String[] ends = lane.split("-");
            for (String end : ends) {
                map.putIfAbsent(end, new ArrayList<>());
            }
            if (ends.length == 2) {
                map.get(ends[0]).add(ends[1]);
                map.get(ends[1]).add(ends[0]);
            }
        }
        return map;
    }

    // Runs the student's search, but gives up after a few seconds, so a search
    // that goes round a loop forever fails with a message instead of hanging.
    @SuppressWarnings("unchecked")
    static <T> T call(String what, Callable<T> body) {
        Object[] result = new Object[1];
        Throwable[] error = new Throwable[1];
        Thread thread = new Thread(() -> {
            try {
                result[0] = body.call();
            } catch (Throwable e) {
                error[0] = e;
            }
        });
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join(4000);
        } catch (InterruptedException e) {
            // ignore
        }
        if (thread.isAlive()) {
            fail(what + " never finished: the search seems to go round in circles. Does it skip waypoints it has already visited?");
        }
        if (error[0] instanceof StackOverflowError) {
            fail(what + " ran out of stack: the search seems to go round in circles. Does it skip waypoints it has already visited?");
        }
        if (error[0] != null) {
            fail(what + " crashed with " + error[0]);
        }
        return (T) result[0];
    }

    static void expect(Map<String, List<String>> map, String from, String to, int want_hops, String what) {
        List<String> got = call("shortestPath(\"" + from + "\", \"" + to + "\")", () -> PathFinder.shortestPath(map, from, to));
        String called = "shortestPath from " + from + " to " + to + " " + what;
        if (got == null) {
            fail(called + " returned null. Return an empty list when there's no path.");
        }
        if (want_hops < 0) {
            if (!got.isEmpty()) {
                fail(called + " was " + got + ", expected an empty list (there's no way there).");
            }
            return;
        }
        if (got.isEmpty()) {
            fail(called + " was an empty list, but there is a path.");
        }
        if (!got.get(0).equals(from) || !got.get(got.size() - 1).equals(to)) {
            fail(called + " was " + got + ". The path should start with " + from + " and end with " + to + ".");
        }
        for (int i = 0; i + 1 < got.size(); i++) {
            List<String> next_to = map.get(got.get(i));
            if (next_to == null || !next_to.contains(got.get(i + 1))) {
                fail(called + " was " + got + ", but no lane joins " + got.get(i) + " and " + got.get(i + 1) + ".");
            }
        }
        int hops = got.size() - 1;
        if (hops != want_hops) {
            fail(called + " was " + got + " (" + hops + " hops), but there's a path with only " + want_hops
                + ". A breadth-first search (a queue, first in, first out) finds the fewest.");
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> chain = field("Hub-Ramp", "Ramp-Wing", "Wing-Tower");
        expect(chain, "Hub", "Tower", 3, "along Hub-Ramp-Wing-Tower");
        expect(chain, "Tower", "Ramp", 2, "along the same lanes backwards");
        expect(chain, "Wing", "Wing", 0, "(from a waypoint to itself is just that waypoint)");

        // Hub's first lane leads the long way round, so following the first lane
        // you see (a depth-first search) finds a 4-hop path. The short way is 3.
        Map<String, List<String>> long_way = field(
            "Hub-Ramp", "Ramp-Wing", "Wing-Tower", "Hub-Dock", "Dock-Tower", "Tower-Goal");
        expect(long_way, "Hub", "Goal", 3, "when Hub's first lane goes the long way round");
        expect(long_way, "Goal", "Hub", 3, "when driven from the other end");
        Map<String, List<String>> long_way_last = field(
            "Hub-Dock", "Dock-Tower", "Tower-Goal", "Hub-Ramp", "Ramp-Wing", "Wing-Tower");
        expect(long_way_last, "Hub", "Goal", 3, "when Hub's last lane goes the long way round");

        Map<String, List<String>> apart = field("Hub-Ramp", "Shed-Dock", "Cage");
        expect(apart, "Hub", "Dock", -1, "when Shed-Dock is a separate piece of the map");
        expect(apart, "Hub", "Cage", -1, "when no lane leads to Cage");
        expect(apart, "Hub", "Nowhere", -1, "when Nowhere isn't on the map");
        expect(apart, "Nowhere", "Hub", -1, "when Nowhere isn't on the map");

        List<String> lanes = new ArrayList<>();
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (col < 4) lanes.add("R" + row + "C" + col + "-R" + row + "C" + (col + 1));
                if (row < 4) lanes.add("R" + row + "C" + col + "-R" + (row + 1) + "C" + col);
            }
        }
        lanes.add("R0C0-R4C3");
        Map<String, List<String>> grid = field(lanes.toArray(new String[0]));
        expect(grid, "R0C0", "R4C4", 2, "across a 5 by 5 grid with a shortcut lane R0C0-R4C3");
        expect(grid, "R0C4", "R4C0", 8, "corner to corner across a 5 by 5 grid");

        System.out.println("shortestPath finds the fewest hops.");
    }
}
