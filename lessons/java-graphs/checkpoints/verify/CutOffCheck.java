import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CutOffCheck {
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

    static void expect(Map<String, List<String>> map, String start, List<String> want, String what) {
        List<String> got = call("unreachable(\"" + start + "\")", () -> PathFinder.unreachable(map, start));
        if (got == null) {
            fail("unreachable from " + start + " " + what + " returned null. Return an empty list when nothing is cut off.");
        }
        if (!want.equals(got)) {
            fail("unreachable from " + start + " " + what + " was " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        expect(field("Hub-Ramp", "Ramp-Wing", "Cage"), "Hub", List.of("Cage"), "when no lane leads to Cage");
        expect(field("Hub-Ramp", "Ramp-Wing", "Wing-Hub"), "Ramp", List.of(), "when every waypoint is joined up");
        expect(field("Hub-Ramp", "Zone-Dock", "Dock-Bay", "Wing"), "Hub", List.of("Bay", "Dock", "Wing", "Zone"),
            "with a separate piece Zone-Dock-Bay and a lone Wing (sorted alphabetically)");
        expect(field("Hub-Ramp", "Zone-Dock", "Dock-Bay", "Wing"), "Dock", List.of("Hub", "Ramp", "Wing"),
            "starting inside the separate piece");
        expect(field("Cage", "Hub-Ramp"), "Cage", List.of("Hub", "Ramp"), "starting on a waypoint with no lanes");
        expect(field("Hub-Ramp", "Wing"), "Nowhere", List.of("Hub", "Ramp", "Wing"),
            "when the start isn't on the map (nothing can be reached)");

        System.out.println("unreachable spots every cut-off waypoint.");
    }
}
