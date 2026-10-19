import java.util.AbstractList;
import java.util.List;

public class LinearSearchCheck {
    // Counts how many different items the student's code looks at.
    static class Watched extends AbstractList<Integer> {
        final List<Integer> items_;
        long looks_ = 0;
        int last_index_ = -1;

        Watched(List<Integer> items) {
            items_ = items;
        }

        @Override
        public Integer get(int index) {
            if (index != last_index_) {
                looks_++;
                last_index_ = index;
            }
            return items_.get(index);
        }

        @Override
        public int size() {
            return items_.size();
        }

        @Override
        public String toString() {
            return items_.toString();
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void check(Integer[] values, int team, int want) {
        Watched teams = new Watched(List.of(values));
        int got = Search.indexOf(teams, team);
        if (got != want) {
            fail("indexOf(" + teams + ", " + team + ") returned " + got + ", expected " + want + ".");
        }
        long allowed = want < 0 ? values.length : want + 1;
        if (teams.looks_ > allowed) {
            fail("indexOf(" + teams + ", " + team + ") looked at " + teams.looks_ + " items, but "
                + (want < 0 ? "the list only has " + values.length + "." : "the team is found after " + allowed + ". Stop as soon as you find it."));
        }
    }

    public static void main(String[] args) {
        try {
            check(new Integer[] {}, 4143, -1);
            check(new Integer[] { 4143 }, 4143, 0);
            check(new Integer[] { 4143 }, 254, -1);
            check(new Integer[] { 971, 118, 2056, 6328 }, 2056, 2);
            check(new Integer[] { 971, 118, 2056, 6328 }, 6328, 3);
            check(new Integer[] { 971, 118, 2056, 6328 }, 971, 0);
            check(new Integer[] { 971, 118, 2056, 6328 }, 1323, -1);
            check(new Integer[] { 5, 3, 8, 3, 3 }, 3, 1);
            check(new Integer[] { 1690, 1114, 1690 }, 1690, 0);

            Integer[] many = new Integer[600];
            for (int i = 0; i < many.length; i++) {
                many[i] = 9000 - i * 11;
            }
            check(many, many[17], 17);
            check(many, many[599], 599);
            check(many, 1, -1);
        } catch (RuntimeException e) {
            fail("indexOf crashed with " + e + ". One of the tests is an empty list.");
        }
        System.out.println("indexOf finds the first match and stops there.");
    }
}
