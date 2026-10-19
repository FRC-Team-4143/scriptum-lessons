public class Main {
    public static void main(String[] args) {
        FieldMap field = new FieldMap();
        field.connect("Start", "Left Lane");
        field.connect("Start", "Center");
        field.connect("Start", "Right Lane");
        field.connect("Left Lane", "Coral Station");
        field.connect("Coral Station", "Reef");
        field.connect("Center", "Reef");
        field.connect("Right Lane", "Reef");
        field.connect("Reef", "Barge");
        // The cage is on the map, but no lane leads to it yet.
        field.addWaypoint("Cage");

        System.out.println("Waypoints on the map: " + field.waypointCount());
        System.out.println("One lane from Start: " + field.neighbors("Start"));
        System.out.println("One lane from Reef: " + field.neighbors("Reef"));
        System.out.println();

        System.out.println("Start -> Barge possible? " + PathFinder.canReach(field.getMap(), "Start", "Barge"));
        System.out.println("Start -> Cage possible?  " + PathFinder.canReach(field.getMap(), "Start", "Cage"));
        System.out.println("Fewest hops Start -> Barge: " + PathFinder.shortestPath(field.getMap(), "Start", "Barge"));
        System.out.println("Fewest hops Coral Station -> Right Lane: "
            + PathFinder.shortestPath(field.getMap(), "Coral Station", "Right Lane"));
        System.out.println("Cut off from Start: " + PathFinder.unreachable(field.getMap(), "Start"));
    }
}
