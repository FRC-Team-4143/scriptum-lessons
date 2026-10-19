import java.util.ArrayList;
import java.util.List;

public class LastOrDefaultCheck {
    // A class the lesson never mentions: lastOrDefault has to work for anything.
    static class Waypoint {
        final String name_;

        Waypoint(String name) {
            name_ = name;
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        List<String> empty = new ArrayList<>();
        String got = ListTools.lastOrDefault(empty, "none");
        if (!"none".equals(got)) {
            fail("lastOrDefault of an empty list with fallback \"none\" was " + got + ", expected \"none\".");
        }

        List<String> autos = List.of("Left", "Center", "Right");
        got = ListTools.lastOrDefault(autos, "none");
        if (!"Right".equals(got)) {
            fail("lastOrDefault of [Left, Center, Right] was " + got + ", expected \"Right\" (the last one).");
        }

        List<Integer> one = List.of(7);
        Integer number = ListTools.lastOrDefault(one, -1);
        if (number == null || number != 7) {
            fail("lastOrDefault of [7] with fallback -1 was " + number + ", expected 7.");
        }

        List<Integer> none = new ArrayList<>();
        number = ListTools.lastOrDefault(none, -1);
        if (number == null || number != -1) {
            fail("lastOrDefault of an empty List<Integer> with fallback -1 was " + number + ", expected -1.");
        }

        Waypoint a = new Waypoint("a");
        Waypoint b = new Waypoint("b");
        Waypoint home = new Waypoint("home");
        Waypoint last = ListTools.lastOrDefault(List.of(a, b), home);
        if (last != b) {
            fail("lastOrDefault of a list of two Waypoints didn't return the second one.");
        }
        if (ListTools.lastOrDefault(List.of(a), null) != a) {
            fail("lastOrDefault of a one-Waypoint list with a null fallback didn't return that Waypoint.");
        }

        System.out.println("lastOrDefault works for a list of any type.");
    }
}
