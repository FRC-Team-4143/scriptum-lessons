import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class FastDuplicatesCheck {
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

    // new Integer objects each time, so == between two equal team numbers is false.
    @SuppressWarnings("removal")
    static Watched of(int... values) {
        List<Integer> items = new ArrayList<>();
        for (int value : values) {
            items.add(new Integer(value));
        }
        return new Watched(items);
    }

    static void check(Watched teams, boolean want) {
        boolean got = Duplicates.hasDuplicate(teams);
        if (got != want) {
            fail("hasDuplicate(" + teams + ") returned " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        try {
            check(of(), false);
            check(of(4143), false);
            check(of(4143, 4143), true);
            check(of(118, 254, 1678), false);
            check(of(2056, 118, 971, 118), true);
            check(of(6328, 1323, 3476, 1690, 6328), true);
            check(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), false);

            // A district's worth of teams, all different.
            int[] numbers = new int[3000];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = 100 + i * 7;
            }
            Watched district = of(numbers);
            check(district, false);
            if (district.looks_ > 2L * numbers.length) {
                fail("hasDuplicate looked at teams " + district.looks_ + " times for a list of " + numbers.length
                    + " different teams. Looking at each team once is enough: remember the ones you've seen in a HashSet.");
            }

            // The same district with the first team typed again at the very end.
            int[] repeat = new int[numbers.length + 1];
            System.arraycopy(numbers, 0, repeat, 0, numbers.length);
            repeat[numbers.length] = numbers[0];
            Watched with_repeat = of(repeat);
            check(with_repeat, true);
            if (with_repeat.looks_ > 2L * repeat.length) {
                fail("hasDuplicate looked at teams " + with_repeat.looks_ + " times for a list of " + repeat.length
                    + " teams. Looking at each team once is enough: remember the ones you've seen in a HashSet.");
            }
        } catch (RuntimeException e) {
            fail("hasDuplicate crashed with " + e + ".");
        }
        System.out.println("hasDuplicate gives the right answer in one pass.");
    }
}
