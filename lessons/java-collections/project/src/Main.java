public class Main {
    public static final int OUR_TEAM = 4143;

    public static void main(String[] args) {
        // What our scouts wrote down: each row is {team number, points}.
        int[][] scouted = {
            {4143, 42}, {254, 65}, {1678, 50},
            {4143, 57}, {254, 48}, {118, 61},
            {1678, 90}, {4143, 38}, {118, 30},
        };

        ScoreList our_scores = new ScoreList();
        TeamsSeen seen = new TeamsSeen();
        TeamTotals totals = new TeamTotals();

        for (int[] row : scouted) {
            int team = row[0];
            int points = row[1];
            seen.addTeam(team);
            totals.addMatch(team, points);
            if (team == OUR_TEAM) {
                our_scores.addScore(points);
            }
        }

        System.out.println("Rows scouted: " + scouted.length);
        System.out.println("Team " + OUR_TEAM + ": " + our_scores.count() + " matches, average "
            + String.format("%.1f", our_scores.average()) + ", best " + our_scores.best());
        System.out.println("Different teams seen: " + seen.countTeams());
        System.out.println("Seen team 254? " + seen.hasSeen(254));
        System.out.println("Seen team 971? " + seen.hasSeen(971));
        System.out.println("Team 1678 total: " + totals.totalFor(1678));
        System.out.println("Team 971 total: " + totals.totalFor(971));
        System.out.println("Top team: " + totals.topTeam());
    }
}
