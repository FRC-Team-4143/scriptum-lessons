import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class MaxCheck {
    // A Comparable class the lesson never mentions. Two readings with the same
    // value compare equal, but they are different objects.
    static class Reading implements Comparable<Reading> {
        final String label_;
        final double value_;

        Reading(String label, double value) {
            label_ = label;
            value_ = value;
        }

        @Override
        public int compareTo(Reading other) {
            return Double.compare(value_, other.value_);
        }

        @Override
        public String toString() {
            return label_;
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expectInt(List<Integer> items, Integer want) {
        Integer got = ListTools.max(items);
        if (want == null ? got != null : !want.equals(got)) {
            fail("max of " + items + " was " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) throws Exception {
        Method method = ListTools.class.getMethod("max", List.class);
        TypeVariable<Method>[] params = method.getTypeParameters();
        Type[] bounds = params.length == 1 ? params[0].getBounds() : new Type[0];
        if (bounds.length == 0 || bounds[0] == Object.class) {
            fail("max works, but its type has no bound. Declare it <T extends Comparable<T>> so only things that can be compared are allowed.");
        }

        expectInt(List.of(3, 9, 2), 9);
        expectInt(List.of(12, 4, 8), 12);
        expectInt(List.of(1, 5, 20), 20);
        expectInt(List.of(-5, -2, -9), -2);
        expectInt(List.of(0), 0);
        expectInt(new ArrayList<>(), null);

        String word = ListTools.max(List.of("intake", "climber", "shooter"));
        if (!"shooter".equals(word)) {
            fail("max of [intake, climber, shooter] was " + word + ", expected \"shooter\" (last alphabetically).");
        }

        Reading low = new Reading("low", 1.5);
        Reading first = new Reading("first 9.0", 9.0);
        Reading second = new Reading("second 9.0", 9.0);
        Reading got = ListTools.max(List.of(low, first, second));
        if (got != first) {
            fail("max of readings [1.5, 9.0, 9.0] returned \"" + got + "\". A tie should keep the first one, \"first 9.0\".");
        }
        got = ListTools.max(List.of(new Reading("a", -3.0), new Reading("b", -1.0)));
        if (got == null || !"b".equals(got.label_)) {
            fail("max of readings [-3.0, -1.0] returned \"" + got + "\", expected \"b\" (-1.0).");
        }

        System.out.println("max finds the biggest of anything Comparable.");
    }
}
