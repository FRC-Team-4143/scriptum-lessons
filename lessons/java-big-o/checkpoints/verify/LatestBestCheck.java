import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class LatestBestCheck {
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

    static void checkLatest(Watched scores, int want) {
        int got = ScoutStats.latest(scores);
        if (got != want) {
            fail("latest(" + scores + ") returned " + got + ", expected " + want + ".");
        }
    }

    static void checkBest(Watched scores, int want) {
        int got = ScoutStats.best(scores);
        if (got != want) {
            fail("best(" + scores + ") returned " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        try {
            checkLatest(of(), 0);
            checkLatest(of(58), 58);
            checkLatest(of(12, 90, 33), 33);
            checkLatest(of(0, 0, 7, 0), 0);

            checkBest(of(), 0);
            checkBest(of(58), 58);
            checkBest(of(12, 90, 33), 90);
            checkBest(of(101, 40, 76), 101);
            checkBest(of(40, 76, 133), 133);
            checkBest(of(65, 65, 65), 65);
            checkBest(of(0, 0), 0);

            // A whole season of scores.
            List<Integer> season = new ArrayList<>();
            for (int i = 0; i < 1500; i++) {
                season.add((i * 37) % 211);
            }
            season.set(900, 250);
            season.set(1499, 19);

            Watched big = new Watched(season);
            checkLatest(big, 19);
            if (big.looks_ > 1) {
                fail("latest looked at " + big.looks_ + " scores in a list of 1500 to find the last one. It should take 1 look: jump straight to it with get.");
            }

            big = new Watched(season);
            checkBest(big, 250);
            if (big.looks_ > 1500) {
                fail("best looked at scores " + big.looks_ + " times in a list of 1500. Looking at each score once should be enough: one loop, O(n).");
            }
        } catch (RuntimeException e) {
            fail("Your code crashed with " + e + ". One of the tests is an empty list.");
        }
        System.out.println("latest takes one step and best takes one pass.");
    }
}
