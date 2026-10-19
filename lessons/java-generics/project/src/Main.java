import java.util.ArrayList;
import java.util.List;

// Main won't compile until Pair, RingBuffer and ListTools are generic.
public class Main {
    public static void main(String[] args) {
        // The last 3 gyro headings, in degrees. The oldest drops out.
        RingBuffer<Double> headings = new RingBuffer<>(3);
        double[] readings = { 0.0, 1.5, 3.1, 4.4, 6.0 };
        for (double reading : readings) {
            headings.add(reading);
        }
        System.out.print("Last " + headings.size() + " headings:");
        for (int i = 0; i < headings.size(); i++) {
            System.out.print(" " + headings.get(i));
        }
        System.out.println();
        System.out.println("Latest heading: " + headings.latest());

        // The same RingBuffer class, holding text this time.
        RingBuffer<String> alerts = new RingBuffer<>(2);
        alerts.add("Battery low");
        alerts.add("Arm stalled");
        alerts.add("Camera lost");
        System.out.println("Latest alert: " + alerts.latest() + " (" + alerts.size() + " kept)");

        // Team number and nickname, kept together.
        List<Pair<Integer, String>> alliance = new ArrayList<>();
        alliance.add(new Pair<>(4143, "MARS/WARS"));
        alliance.add(new Pair<>(254, "The Cheesy Poofs"));
        alliance.add(new Pair<>(1678, "Citrus Circuits"));
        for (Pair<Integer, String> team : alliance) {
            System.out.println("Team " + team.getFirst() + " is " + team.getSecond());
        }

        // One method, two kinds of list.
        List<String> autos_picked = new ArrayList<>();
        System.out.println("Auto: " + ListTools.lastOrDefault(autos_picked, "Leave the zone"));
        autos_picked.add("Two-piece center");
        System.out.println("Auto: " + ListTools.lastOrDefault(autos_picked, "Leave the zone"));

        List<Integer> scores = List.of(42, 61, 55, 38);
        System.out.println("Best score: " + ListTools.max(scores));
        List<String> autos = List.of("Amp side", "Center", "Source side");
        System.out.println("Last auto alphabetically: " + ListTools.max(autos));
    }
}
