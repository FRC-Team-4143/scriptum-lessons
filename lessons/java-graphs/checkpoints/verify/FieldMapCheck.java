import java.util.List;

public class FieldMapCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectNeighbors(FieldMap map, String name, List<String> want, String what) {
        List<String> got = map.neighbors(name);
        if (got == null) {
            fail("neighbors(\"" + name + "\") returned null " + what + ". Return an empty list instead.");
        }
        if (!want.equals(got)) {
            fail("neighbors(\"" + name + "\") was " + got + " " + what + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        FieldMap map = new FieldMap();
        map.connect("Hub", "Ramp");
        expectNeighbors(map, "Hub", List.of("Ramp"), "after connect(\"Hub\", \"Ramp\")");
        expectNeighbors(map, "Ramp", List.of("Hub"),
            "after connect(\"Hub\", \"Ramp\") (a lane goes both ways, so Ramp's list needs Hub too)");
        if (map.waypointCount() != 2) {
            fail("After connect(\"Hub\", \"Ramp\") the map had " + map.waypointCount() + " waypoints, expected 2.");
        }

        map.connect("Hub", "Wing");
        map.connect("Tower", "Hub");
        expectNeighbors(map, "Hub", List.of("Ramp", "Wing", "Tower"),
            "after connecting Hub to Ramp, then Wing, then Tower (in the order they were connected)");

        map.connect("Hub", "Ramp");
        map.connect("Ramp", "Hub");
        expectNeighbors(map, "Hub", List.of("Ramp", "Wing", "Tower"),
            "after connecting Hub and Ramp a second and third time (each lane is listed once)");
        expectNeighbors(map, "Ramp", List.of("Hub"), "after connecting Hub and Ramp three times");

        map.addWaypoint("Shed");
        if (map.waypointCount() != 5) {
            fail("After addWaypoint(\"Shed\") the map had " + map.waypointCount() + " waypoints, expected 5.");
        }
        expectNeighbors(map, "Shed", List.of(), "for a waypoint added with no lanes");

        map.addWaypoint("Hub");
        expectNeighbors(map, "Hub", List.of("Ramp", "Wing", "Tower"),
            "after addWaypoint(\"Hub\") on a waypoint already on the map (it should keep its lanes)");
        if (map.waypointCount() != 5) {
            fail("addWaypoint(\"Hub\") on a waypoint already on the map changed the count to " + map.waypointCount() + ", expected it to stay 5.");
        }

        expectNeighbors(map, "Nowhere", List.of(), "for a waypoint that isn't on the map");
        if (map.waypointCount() != 5) {
            fail("Asking for neighbors(\"Nowhere\") added it to the map. Only addWaypoint and connect should add waypoints.");
        }

        System.out.println("The field map joins waypoints both ways.");
    }
}
