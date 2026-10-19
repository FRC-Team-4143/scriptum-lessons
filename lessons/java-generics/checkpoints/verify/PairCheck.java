public class PairCheck {
    // A class the lesson never mentions: Pair has to hold anything.
    static class Waypoint {
        final double x_;

        Waypoint(double x) {
            x_ = x;
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        if (Pair.class.getTypeParameters().length != 2) {
            fail("Pair should have two type parameters: public class Pair<A, B>.");
        }

        Pair<String, Double> shot = new Pair<>("Podium", 4.25);
        String name = shot.getFirst();
        Double meters = shot.getSecond();
        if (!"Podium".equals(name) || meters == null || meters != 4.25) {
            fail("new Pair<>(\"Podium\", 4.25) gave getFirst() = " + name + " and getSecond() = " + meters
                + ", expected \"Podium\" and 4.25.");
        }

        Pair<String, String> sides = new Pair<>("left", "right");
        if (!"left".equals(sides.getFirst()) || !"right".equals(sides.getSecond())) {
            fail("new Pair<>(\"left\", \"right\") gave getFirst() = " + sides.getFirst() + " and getSecond() = "
                + sides.getSecond() + ". getFirst() should give the first value, getSecond() the second.");
        }

        Waypoint start = new Waypoint(1.0);
        Pair<Waypoint, Boolean> stop = new Pair<>(start, true);
        Waypoint got = stop.getFirst();
        Boolean flag = stop.getSecond();
        if (got != start || flag == null || !flag) {
            fail("A Pair<Waypoint, Boolean> didn't give back the same waypoint and true.");
        }

        Pair<Integer, String> empty = new Pair<>(4143, null);
        Integer team = empty.getFirst();
        if (team == null || team != 4143 || empty.getSecond() != null) {
            fail("new Pair<>(4143, null) gave getFirst() = " + team + " and getSecond() = " + empty.getSecond()
                + ", expected 4143 and null.");
        }

        System.out.println("Pair<A, B> holds two values of any types.");
    }
}
