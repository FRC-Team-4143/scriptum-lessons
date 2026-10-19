import java.util.ArrayList;
import java.util.Arrays;

public class ParseAllCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(String[] lines, int[] good_teams, Integer[] bad, String what) {
        ScoutReport report;
        try {
            report = ScoutParser.parseAll(lines);
        } catch (RuntimeException e) {
            fail("parseAll of " + what + " crashed with " + e.getClass().getSimpleName()
                + ". It should skip bad lines and keep going.");
            return;
        }
        ArrayList<Integer> teams = new ArrayList<>();
        for (ScoutEntry entry : report.getEntries()) {
            teams.add(entry == null ? null : entry.getTeam());
        }
        ArrayList<Integer> want_teams = new ArrayList<>();
        for (int t : good_teams) {
            want_teams.add(t);
        }
        if (!teams.equals(want_teams)) {
            fail("parseAll of " + what + " kept entries for teams " + teams + ", expected " + want_teams + ".");
        }
        if (!report.getBadLines().equals(Arrays.asList(bad))) {
            fail("parseAll of " + what + " marked lines " + report.getBadLines() + " as bad, expected "
                + Arrays.asList(bad) + ". Lines count from 1, and blank lines aren't bad.");
        }
    }

    public static void main(String[] args) {
        ScoutEntry probe = null;
        try {
            probe = ScoutParser.parseLine("4143,1,2", 1);
        } catch (Exception e) {
            // handled below
        }
        if (probe == null) {
            fail("parseAll uses your parseLine, and parseLine(\"4143,1,2\", 1) doesn't give a ScoutEntry yet. Finish parseLine first.");
        }

        expect(new String[] { "1,1,10", "2,1,20", "3,1,30" }, new int[] { 1, 2, 3 }, new Integer[0],
            "three good lines");
        expect(new String[] { "5,1,10", "oops", "6,1,20" }, new int[] { 5, 6 }, new Integer[] { 2 },
            "a bad line in the middle");
        expect(new String[] { "x,y,z", "7,2,0", "8,2" }, new int[] { 7 }, new Integer[] { 1, 3 },
            "bad first and last lines");
        expect(new String[] { "", "   ", "9,3,44", "" }, new int[] { 9 }, new Integer[0],
            "blank lines around one good line");
        expect(new String[] { "a", "", "b,1,1", "10,4,12" }, new int[] { 10 }, new Integer[] { 1, 3 },
            "bad lines with a blank one between them");
        expect(new String[] { "nope", "still nope" }, new int[0], new Integer[] { 1, 2 },
            "only bad lines");
        expect(new String[0], new int[0], new Integer[0], "no lines at all");

        System.out.println("parseAll skips bad lines, counts them and keeps going.");
    }
}
