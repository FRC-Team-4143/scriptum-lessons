public class TopTeamCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    // Each row is {team, points}, added in order.
    static void expect(int[][] matches, int want, String what) {
        TeamTotals totals = new TeamTotals();
        for (int[] match : matches) {
            totals.addMatch(match[0], match[1]);
        }
        int got = totals.topTeam();
        if (got != want) {
            fail("topTeam() with " + what + " was " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        expect(new int[0][], -1, "no matches");
        expect(new int[][] { {118, 50} }, 118, "one match for team 118");
        expect(new int[][] { {118, 50}, {254, 120}, {1678, 80} }, 254,
            "118 at 50, 254 at 120 and 1678 at 80");
        expect(new int[][] { {254, 90}, {118, 60}, {118, 60} }, 118,
            "254 at 90 and 118 at 60 + 60 (add up each team's matches)");
        expect(new int[][] { {4143, 100}, {1678, 200}, {4143, 100} }, 1678,
            "4143 and 1678 tied at 200 (a tie goes to the lower team number)");
        expect(new int[][] { {1678, 200}, {4143, 200} }, 1678,
            "1678 and 4143 tied at 200, added the other way round (a tie goes to the lower team number)");
        expect(new int[][] { {4161, 75}, {4146, 75}, {33, 10} }, 4146,
            "4161 and 4146 tied at 75 (a tie goes to the lower team number)");

        System.out.println("topTeam finds the highest-scoring team.");
    }
}
