import java.lang.reflect.Field;
import java.util.Map;

public class TeamTotalsCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        boolean has_map = false;
        for (Field field : TeamTotals.class.getDeclaredFields()) {
            if (Map.class.isAssignableFrom(field.getType())) {
                has_map = true;
            }
        }
        if (!has_map) {
            fail("TeamTotals has no map field yet. Import java.util.HashMap and add \"protected HashMap<Integer, Integer> totals_ = new HashMap<>();\".");
        }

        TeamTotals empty = new TeamTotals();
        if (empty.totalFor(254) != 0) {
            fail("A new TeamTotals's totalFor(254) was " + empty.totalFor(254) + ", expected 0 (no matches yet).");
        }

        TeamTotals totals = new TeamTotals();
        totals.addMatch(254, 40);
        if (totals.totalFor(254) != 40) {
            fail("After addMatch(254, 40), totalFor(254) was " + totals.totalFor(254) + ", expected 40.");
        }
        totals.addMatch(1678, 30);
        totals.addMatch(254, 25);
        if (totals.totalFor(254) != 65) {
            fail("After addMatch(254, 40) and addMatch(254, 25), totalFor(254) was " + totals.totalFor(254)
                + ", expected 65. addMatch should add to the total, not replace it.");
        }
        if (totals.totalFor(1678) != 30) {
            fail("After addMatch(1678, 30), totalFor(1678) was " + totals.totalFor(1678) + ", expected 30.");
        }
        if (totals.totalFor(118) != 0) {
            fail("totalFor(118) was " + totals.totalFor(118) + ", expected 0: team 118 has no matches.");
        }
        if (new TeamTotals().totalFor(254) != 0) {
            fail("A brand-new TeamTotals already had points for 254. Is totals_ static? Each TeamTotals needs its own map.");
        }

        System.out.println("TeamTotals adds up each team's points.");
    }
}
