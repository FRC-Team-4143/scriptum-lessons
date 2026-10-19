import java.lang.reflect.Method;

public class SpeedFiltersCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(SpeedFilter filter, double speed, double want, String what) {
        double got = filter.apply(speed);
        if (Math.abs(got - want) > 0.0001) {
            fail(what + ".apply(" + speed + ") was " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        if (!SpeedFilter.class.isInterface()) {
            fail("SpeedFilter should be declared as \"public interface SpeedFilter\", not a class.");
        }
        if (!SpeedFilter.class.isAnnotationPresent(FunctionalInterface.class)) {
            fail("Put @FunctionalInterface on the line above \"public interface SpeedFilter\".");
        }
        try {
            Method apply = SpeedFilter.class.getMethod("apply", double.class);
            if (apply.getReturnType() != double.class) {
                fail("SpeedFilter's apply(double speed) should return a double.");
            }
        } catch (NoSuchMethodException e) {
            fail("SpeedFilter needs one method: double apply(double speed);");
        }

        // A lambda the lesson never mentions: SpeedFilter has to work for any of them.
        SpeedFilter triple = speed -> speed * 3;
        expect(triple, 0.2, 0.6, "A SpeedFilter lambda speed -> speed * 3");

        SpeedFilter half = Filters.half();
        if (half == null) {
            fail("Filters.half() returned null. Return a lambda that halves the speed.");
        }
        expect(half, 0.8, 0.4, "Filters.half()");
        expect(half, -1.0, -0.5, "Filters.half()");

        SpeedFilter invert = Filters.invert();
        if (invert == null) {
            fail("Filters.invert() returned null. Return a lambda that flips the sign of the speed.");
        }
        expect(invert, 0.3, -0.3, "Filters.invert()");
        expect(invert, -0.75, 0.75, "Filters.invert()");

        SpeedFilter small = Filters.deadband(0.1);
        if (small == null) {
            fail("Filters.deadband(0.1) returned null. Return a lambda with a { } body.");
        }
        expect(small, 0.05, 0.0, "Filters.deadband(0.1)");
        expect(small, -0.09, 0.0, "Filters.deadband(0.1) (a small NEGATIVE speed counts too: use Math.abs)");
        expect(small, 0.5, 0.5, "Filters.deadband(0.1)");
        expect(small, -0.7, -0.7, "Filters.deadband(0.1)");
        expect(small, 0.1, 0.1, "Filters.deadband(0.1) (exactly the threshold is not below it)");

        // A different threshold proves the lambda uses threshold, not a fixed 0.1.
        SpeedFilter wide = Filters.deadband(0.25);
        if (wide == null) {
            fail("Filters.deadband(0.25) returned null.");
        }
        expect(wide, 0.2, 0.0, "Filters.deadband(0.25)");
        expect(wide, 0.3, 0.3, "Filters.deadband(0.25)");

        System.out.println("SpeedFilter is a functional interface, and the filters are lambdas.");
    }
}
