import java.lang.reflect.Field;
import java.util.Set;

public class TeamsSeenCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        boolean has_set = false;
        for (Field field : TeamsSeen.class.getDeclaredFields()) {
            if (Set.class.isAssignableFrom(field.getType())) {
                has_set = true;
            }
        }
        if (!has_set) {
            fail("TeamsSeen has no set field yet. Import java.util.HashSet and add \"protected HashSet<Integer> teams_ = new HashSet<>();\": a set skips repeats for you.");
        }

        TeamsSeen empty = new TeamsSeen();
        if (empty.countTeams() != 0) {
            fail("A new TeamsSeen's countTeams() was " + empty.countTeams() + ", expected 0.");
        }
        if (empty.hasSeen(254)) {
            fail("A new TeamsSeen says hasSeen(254) is true, but no teams were added.");
        }

        TeamsSeen seen = new TeamsSeen();
        seen.addTeam(254);
        seen.addTeam(1678);
        seen.addTeam(254);
        if (seen.countTeams() != 2) {
            fail("After adding 254, 1678 and 254 again, countTeams() was " + seen.countTeams() + ", expected 2 (254 counts once).");
        }
        if (!seen.hasSeen(254) || !seen.hasSeen(1678)) {
            fail("After adding 254 and 1678, hasSeen should be true for both.");
        }
        if (seen.hasSeen(118)) {
            fail("hasSeen(118) was true, but 118 was never added.");
        }
        seen.addTeam(4143);
        seen.addTeam(4143);
        seen.addTeam(4143);
        if (seen.countTeams() != 3) {
            fail("After adding 254, 1678, 254, then 4143 three times, countTeams() was " + seen.countTeams() + ", expected 3.");
        }
        if (new TeamsSeen().countTeams() != 0) {
            fail("A brand-new TeamsSeen already had teams in it. Is teams_ static? Each TeamsSeen needs its own set.");
        }

        System.out.println("TeamsSeen counts each team once.");
    }
}
