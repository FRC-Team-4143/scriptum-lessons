import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class BinarySearchCheck {
    static class UsedLibrary extends RuntimeException {
    }

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
            for (StackTraceElement frame : new Throwable().getStackTrace()) {
                if (frame.getClassName().startsWith("java.util.") && frame.getMethodName().contains("binarySearch")) {
                    throw new UsedLibrary();
                }
            }
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
            return items_.size() <= 12 ? items_.toString() : "a sorted list of " + items_.size() + " teams";
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    // The most looks a binary search needs: floor(log2(n)) + 1, plus one spare.
    static long allowed(int n) {
        long looks = 1;
        while (n > 1) {
            n = n / 2;
            looks++;
        }
        return looks + 1;
    }

    static void check(List<Integer> values, int team, int want) {
        Watched sorted = new Watched(values);
        int got = Search.binarySearch(sorted, team);
        if (got != want) {
            fail("binarySearch(" + sorted + ", " + team + ") returned " + got + ", expected " + want + ".");
        }
        if (sorted.looks_ > allowed(values.size())) {
            fail("binarySearch looked at " + sorted.looks_ + " items to search for " + team + " in " + sorted
                + ". It should need at most about " + allowed(values.size()) + ": look at the middle, then throw away the half it can't be in.");
        }
    }

    static List<Integer> teams(int count, int start, int gap) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(start + i * gap);
        }
        return list;
    }

    public static void main(String[] args) {
        try {
            check(List.of(), 4143, -1);
            check(List.of(4143), 4143, 0);
            check(List.of(4143), 254, -1);
            check(List.of(4143), 9999, -1);
            check(List.of(118, 254), 118, 0);
            check(List.of(118, 254), 254, 1);
            check(List.of(118, 254), 200, -1);

            // Every team, and every gap between teams, in some small lists.
            for (int size : new int[] { 3, 7, 8, 17 }) {
                List<Integer> list = teams(size, 100, 10);
                for (int i = 0; i < size; i++) {
                    check(list, list.get(i), i);
                    check(list, list.get(i) + 5, -1);
                }
                check(list, 50, -1);
            }

            for (int size : new int[] { 1000, 1024, 4097 }) {
                List<Integer> list = teams(size, 3, 7);
                check(list, list.get(0), 0);
                check(list, list.get(size - 1), size - 1);
                check(list, list.get(size / 3), size / 3);
                check(list, list.get(size - 2), size - 2);
                check(list, 1, -1);
                check(list, list.get(size - 1) + 1, -1);
                check(list, list.get(size / 2) + 3, -1);
            }
        } catch (UsedLibrary e) {
            fail("binarySearch called Java's own binarySearch. Write the loop yourself this time: keep low and high, and look at the middle.");
        } catch (RuntimeException e) {
            fail("binarySearch crashed with " + e + ". Check what it does with an empty list, and that low and high stay inside the list.");
        }
        System.out.println("binarySearch finds teams in about log2(n) looks.");
    }
}
