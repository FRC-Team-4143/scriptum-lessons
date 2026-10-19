import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class TeamsInBothCheck {
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

    static Watched of(Integer... values) {
        return new Watched(List.of(values));
    }

    static void check(Watched event1, Watched event2, int want) {
        int got = EventOverlap.teamsInBoth(event1, event2);
        if (got != want) {
            fail("teamsInBoth(" + event1 + ", " + event2 + ") returned " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        try {
            check(of(), of(), 0);
            check(of(254, 118), of(), 0);
            check(of(), of(254, 118), 0);
            check(of(254, 1678, 118), of(118, 4143, 254), 2);
            check(of(971, 973), of(1323, 2910), 0);
            check(of(4143, 4143, 2056), of(4143), 1);
            check(of(4143), of(4143, 4143, 4143), 1);
            check(of(1, 2, 3, 2, 1), of(3, 2, 1, 1), 3);

            // Two big events, 2000 teams each, 500 of them at both.
            List<Integer> first = new ArrayList<>();
            List<Integer> second = new ArrayList<>();
            for (int i = 0; i < 2000; i++) {
                first.add(10000 + i);
                second.add(11500 + i);
            }
            Watched event1 = new Watched(first);
            Watched event2 = new Watched(second);
            check(event1, event2, 500);
            long allowed1 = 2L * first.size();
            long allowed2 = 2L * second.size();
            if (event1.looks_ > allowed1 || event2.looks_ > allowed2) {
                fail("For two events of 2000 teams, teamsInBoth looked at the first list " + event1.looks_
                    + " times and the second " + event2.looks_ + " times. About 2000 each is enough."
                    + " contains() on a List searches the whole list; put one event's teams in a HashSet first.");
            }
        } catch (RuntimeException e) {
            fail("teamsInBoth crashed with " + e + ". It shouldn't change the lists it is given.");
        }
        System.out.println("teamsInBoth counts each shared team once, in O(n + m).");
    }
}
