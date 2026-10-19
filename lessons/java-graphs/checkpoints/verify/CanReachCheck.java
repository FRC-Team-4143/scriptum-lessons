import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CanReachCheck {
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

    static void expect(Map<String, List<String>> map, String from, String to, boolean want, String what) {
        Boolean got = call("canReach(\"" + from + "\", \"" + to + "\")", () -> PathFinder.canReach(map, from, to));
        if (got != want) {
            fail("canReach from " + from + " to " + to + " " + what + " was " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> chain = field("Hub-Ramp", "Ramp-Wing", "Wing-Tower");
        expect(chain, "Hub", "Ramp", true, "(one lane apart)");
        expect(chain, "Hub", "Tower", true, "(three lanes apart: Hub-Ramp-Wing-Tower)");
        expect(chain, "Tower", "Hub", true, "(the same lanes, driven backwards)");
        expect(chain, "Wing", "Wing", true, "(a waypoint can always reach itself)");

        Map<String, List<String>> loop = field("Hub-Ramp", "Ramp-Wing", "Wing-Hub", "Shed-Dock");
        expect(loop, "Hub", "Wing", true, "on a map with a loop Hub-Ramp-Wing-Hub");
        expect(loop, "Hub", "Dock", false, "when Shed-Dock is a separate piece of the map with a loop next to it");
        expect(loop, "Dock", "Shed", true, "on a separate piece of the map");

        Map<String, List<String>> alone = field("Hub-Ramp", "Cage");
        expect(alone, "Hub", "Cage", false, "when no lane leads to Cage");
        expect(alone, "Cage", "Cage", true, "when Cage has no lanes (it can still reach itself)");
        expect(alone, "Hub", "Nowhere", false, "when Nowhere isn't on the map");
        expect(alone, "Nowhere", "Hub", false, "when Nowhere isn't on the map");
        expect(alone, "Nowhere", "Nowhere", false, "when Nowhere isn't on the map at all");

        // A grid of lanes has many loops; a search that forgets where it's been never ends.
        List<String> lanes = new ArrayList<>();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                if (col < 5) lanes.add("R" + row + "C" + col + "-R" + row + "C" + (col + 1));
                if (row < 5) lanes.add("R" + row + "C" + col + "-R" + (row + 1) + "C" + col);
            }
        }
        lanes.add("Pit");
        Map<String, List<String>> grid = field(lanes.toArray(new String[0]));
        expect(grid, "R0C0", "R5C5", true, "across a 6 by 6 grid of lanes");
        expect(grid, "R0C0", "Pit", false, "on a 6 by 6 grid when Pit has no lanes");

        System.out.println("canReach finds its way, loops and all.");
    }
}
