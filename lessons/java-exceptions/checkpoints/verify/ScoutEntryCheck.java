public class ScoutEntryCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectGood(int team, int match, int points) {
        String call = "new ScoutEntry(" + team + ", " + match + ", " + points + ")";
        ScoutEntry entry;
        try {
            entry = new ScoutEntry(team, match, points);
        } catch (RuntimeException e) {
            fail(call + " threw " + e.getClass().getSimpleName() + ", but those numbers are fine.");
            return;
        }
        if (entry.getTeam() != team || entry.getMatch() != match || entry.getPoints() != points) {
            fail(call + " didn't keep its numbers: got team " + entry.getTeam() + ", match "
                + entry.getMatch() + ", points " + entry.getPoints() + ".");
        }
    }

    static void expectRefused(int team, int match, int points, String why) {
        String call = "new ScoutEntry(" + team + ", " + match + ", " + points + ")";
        try {
            new ScoutEntry(team, match, points);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() == null || e.getMessage().trim().isEmpty()) {
                fail(call + " threw an IllegalArgumentException with no message. Pass a message that says what was wrong.");
            }
            return;
        } catch (RuntimeException e) {
            fail(call + " threw " + e.getClass().getSimpleName() + ", expected an IllegalArgumentException.");
        }
        fail(call + " was allowed, but " + why + ", so it should throw an IllegalArgumentException.");
    }

    public static void main(String[] args) {
        expectGood(4143, 12, 38);
        expectGood(1, 1, 0);
        expectGood(9999, 80, 150);

        expectRefused(0, 5, 10, "team 0 isn't a real team");
        expectRefused(-254, 5, 10, "a team number can't be negative");
        expectRefused(1678, 0, 10, "there is no match 0");
        expectRefused(1678, -3, 10, "a match number can't be negative");
        expectRefused(1678, 7, -1, "a team can't score -1 points");

        System.out.println("ScoutEntry refuses numbers that can't be right.");
    }
}
