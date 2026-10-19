import java.lang.reflect.Field;
import java.util.List;

public class ScoreListCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static ScoreList listOf(int... scores) {
        ScoreList list = new ScoreList();
        for (int score : scores) {
            list.addScore(score);
        }
        return list;
    }

    public static void main(String[] args) {
        boolean has_list = false;
        for (Field field : ScoreList.class.getDeclaredFields()) {
            if (List.class.isAssignableFrom(field.getType())) {
                has_list = true;
            }
        }
        if (!has_list) {
            fail("ScoreList has no list field yet. Import java.util.ArrayList and add \"protected ArrayList<Integer> scores_ = new ArrayList<>();\" to keep the scores in.");
        }

        ScoreList empty = new ScoreList();
        if (empty.count() != 0) {
            fail("A new ScoreList's count() was " + empty.count() + ", expected 0.");
        }
        if (Math.abs(empty.average()) > 0.0001) {
            fail("A new ScoreList's average() was " + empty.average() + ", expected 0.0 (no scores yet).");
        }
        if (empty.best() != 0) {
            fail("A new ScoreList's best() was " + empty.best() + ", expected 0 (no scores yet).");
        }

        ScoreList three = listOf(10, 20, 25);
        if (three.count() != 3) {
            fail("After adding 10, 20 and 25, count() was " + three.count() + ", expected 3.");
        }
        if (Math.abs(three.average() - 55.0 / 3) > 0.0001) {
            String hint = Math.abs(three.average() - 18.0) < 0.0001
                ? " It looks like an int was divided by an int, which drops the decimals."
                : "";
            fail("After adding 10, 20 and 25, average() was " + three.average() + ", expected about 18.33." + hint);
        }
        if (three.best() != 25) {
            fail("After adding 10, 20 and 25, best() was " + three.best() + ", expected 25.");
        }

        ScoreList first_biggest = listOf(40, 5, 12);
        if (first_biggest.best() != 40) {
            fail("After adding 40, 5 and 12, best() was " + first_biggest.best() + ", expected 40 (the best one came first).");
        }
        ScoreList one = listOf(7);
        if (one.count() != 1 || one.best() != 7 || Math.abs(one.average() - 7.0) > 0.0001) {
            fail("With one score of 7, count(), average() and best() should be 1, 7.0 and 7, but were "
                + one.count() + ", " + one.average() + " and " + one.best() + ".");
        }
        if (new ScoreList().count() != 0) {
            fail("A brand-new ScoreList already had scores in it. Is scores_ static? Each ScoreList needs its own list.");
        }

        System.out.println("ScoreList counts, averages and finds the best score.");
    }
}
