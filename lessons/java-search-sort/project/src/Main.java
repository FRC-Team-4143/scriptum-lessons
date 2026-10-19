import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // The order teams checked in at the event, so not sorted.
        List<Integer> check_in = List.of(4143, 254, 1678, 118, 2056, 971);
        System.out.println("1678 checked in at index " + Search.indexOf(check_in, 1678));
        System.out.println("6328 checked in at index " + Search.indexOf(check_in, 6328));

        // 1024 team numbers in order, like the pit map.
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            numbers.add(100 + i * 9);
        }
        CountingList pits = new CountingList(numbers);
        int target = numbers.get(900);

        int linear = Search.indexOf(pits, target);
        System.out.println("Linear search found " + target + " at " + linear + " after " + pits.getLooks() + " looks");
        pits.resetLooks();
        int binary = Search.binarySearch(pits, target);
        System.out.println("Binary search found " + target + " at " + binary + " after " + pits.getLooks() + " looks");

        TeamScore[] scouted = {
            new TeamScore(118, 64),
            new TeamScore(4143, 81),
            new TeamScore(254, 97),
            new TeamScore(2056, 81),
            new TeamScore(971, 52),
        };
        Sorting.sortByScore(scouted);
        System.out.println("Sorted by hand: " + Arrays.toString(scouted));

        List<TeamScore> ranked = Sorting.rankings(List.of(
            new TeamScore(6328, 70),
            new TeamScore(1678, 88),
            new TeamScore(3476, 70),
            new TeamScore(1323, 91)));
        System.out.println("Rankings: " + ranked);
    }
}
