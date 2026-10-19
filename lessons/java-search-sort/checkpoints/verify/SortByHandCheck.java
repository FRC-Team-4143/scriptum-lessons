import java.util.Arrays;
import java.util.IdentityHashMap;

public class SortByHandCheck {
    static class UsedLibrary extends RuntimeException {
    }

    // A TeamScore that notices when Java's own sorting code asks for its score.
    static class Watched extends TeamScore {
        Watched(int team, int score) {
            super(team, score);
        }

        @Override
        public int getScore() {
            for (StackTraceElement frame : new Throwable().getStackTrace()) {
                String name = frame.getClassName();
                if (name.startsWith("java.util.")) {
                    throw new UsedLibrary();
                }
            }
            return super.getScore();
        }

        // The score, for the checker's own use.
        int superScore() {
            return super.getScore();
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static String scores(TeamScore[] teams) {
        int[] values = new int[teams.length];
        for (int i = 0; i < teams.length; i++) {
            values[i] = teams[i] == null ? -1 : ((Watched) teams[i]).superScore();
        }
        return Arrays.toString(values);
    }

    static void check(int... values) {
        Watched[] teams = new Watched[values.length];
        for (int i = 0; i < values.length; i++) {
            teams[i] = new Watched(1000 + i, values[i]);
        }
        TeamScore[] sorted = teams.clone();
        String before = scores(sorted);
        Sorting.sortByScore(sorted);

        // The same teams must all still be there, each exactly once.
        IdentityHashMap<TeamScore, Boolean> seen = new IdentityHashMap<>();
        for (TeamScore team : sorted) {
            if (team == null || seen.put(team, true) != null) {
                fail("After sortByScore on scores " + before + ", some teams were lost or copied: " + scores(sorted)
                    + ". Sorting should only swap teams around.");
            }
        }
        for (int i = 0; i + 1 < sorted.length; i++) {
            if (((Watched) sorted[i]).superScore() < ((Watched) sorted[i + 1]).superScore()) {
                String shown = values.length <= 12 ? " gave " + scores(sorted) : ", spot " + i + " has "
                    + ((Watched) sorted[i]).superScore() + " but spot " + (i + 1) + " has " + ((Watched) sorted[i + 1]).superScore();
                fail("sortByScore on scores " + (values.length <= 12 ? before : "of " + values.length + " teams") + shown
                    + ". The highest score should come first.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            check();
            check(50);
            check(20, 90);
            check(90, 20);
            check(10, 20, 30, 40, 50);
            check(50, 40, 30, 20, 10);
            check(33, 81, 12, 81, 64, 0, 97);
            check(7, 7, 7, 7);
            check(0, 100, 0, 100, 50);

            int[] many = new int[60];
            int value = 17;
            for (int i = 0; i < many.length; i++) {
                value = (value * 31 + 7) % 101;
                many[i] = value;
            }
            check(many);
        } catch (UsedLibrary e) {
            fail("sortByScore used Java's own sorting. Write the loops yourself here (selection or insertion sort); rankings is where List.sort goes.");
        } catch (RuntimeException e) {
            fail("sortByScore crashed with " + e + ". Check that your loops stay inside the array, and that an empty array works.");
        }
        System.out.println("sortByScore puts the highest scores first.");
    }
}
