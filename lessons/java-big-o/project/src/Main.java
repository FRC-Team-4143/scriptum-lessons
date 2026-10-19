import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> scores = List.of(42, 87, 63, 95, 71);
        System.out.println("Latest score: " + ScoutStats.latest(scores));
        System.out.println("Best score: " + ScoutStats.best(scores));

        // 2000 different team numbers: the worst case, because no
        // duplicate turns up to let the check stop early.
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            numbers.add(1000 + i * 3);
        }
        CountingList teams = new CountingList(numbers);

        boolean slow = Duplicates.hasDuplicateSlow(teams);
        System.out.println("Slow duplicate check: " + slow + ", after " + teams.getLooks() + " looks");

        teams.resetLooks();
        boolean fast = Duplicates.hasDuplicate(teams);
        System.out.println("Fast duplicate check: " + fast + ", after " + teams.getLooks() + " looks");

        List<Integer> week1 = List.of(4143, 254, 1678, 118, 4143, 2056);
        List<Integer> week2 = List.of(1678, 971, 4143, 6328, 254);
        System.out.println("Teams at both events: " + EventOverlap.teamsInBoth(week1, week2));
    }
}
