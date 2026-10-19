import java.util.ArrayList;
import java.util.List;

public class RankingsCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static List<TeamScore> list(int... pairs) {
        List<TeamScore> teams = new ArrayList<>();
        for (int i = 0; i < pairs.length; i += 2) {
            teams.add(new TeamScore(pairs[i], pairs[i + 1]));
        }
        return teams;
    }

    static void check(List<TeamScore> input, List<TeamScore> want) {
        List<TeamScore> before = new ArrayList<>(input);
        List<TeamScore> got = Sorting.rankings(input);
        if (got == null) {
            fail("rankings(" + before + ") returned null, expected " + want + ".");
        }
        if (!before.equals(input)) {
            fail("rankings changed the list it was given: " + before + " became " + input
                + ". Copy it first with new ArrayList<>(teams) and sort the copy.");
        }
        if (got.size() != want.size()) {
            fail("rankings(" + before + ") returned " + got.size() + " teams, expected " + want.size() + ".");
        }
        for (int i = 0; i < want.size(); i++) {
            TeamScore a = got.get(i);
            TeamScore b = want.get(i);
            if (a.getTeam() != b.getTeam() || a.getScore() != b.getScore()) {
                fail("rankings(" + before + ") returned " + got + ", expected " + want
                    + ". Highest score first; on a tie, the lower team number first.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            check(list(), list());
            check(list(4143, 60), list(4143, 60));
            check(list(118, 40, 254, 95, 971, 70), list(254, 95, 971, 70, 118, 40));
            check(list(6328, 70, 1678, 88, 3476, 70, 1323, 91), list(1323, 91, 1678, 88, 3476, 70, 6328, 70));
            check(list(2910, 55, 1114, 55, 2056, 55), list(1114, 55, 2056, 55, 2910, 55));
            check(list(8, 0, 9, 100, 7, 0), list(9, 100, 7, 0, 8, 0));

            // An unchangeable list, like List.of(...) in Main.
            List<TeamScore> fixed = List.of(new TeamScore(4414, 30), new TeamScore(195, 75));
            List<TeamScore> got = Sorting.rankings(fixed);
            if (got.size() != 2 || got.get(0).getTeam() != 195) {
                fail("rankings(" + fixed + ") returned " + got + ", expected [195 (75), 4414 (30)].");
            }
        } catch (UnsupportedOperationException e) {
            fail("rankings tried to change the list it was given, which can't be changed. Copy it first with new ArrayList<>(teams) and sort the copy.");
        } catch (RuntimeException e) {
            fail("rankings crashed with " + e + ".");
        }
        System.out.println("rankings orders teams by score, then team number.");
    }
}
