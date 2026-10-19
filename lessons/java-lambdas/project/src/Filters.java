// Ready-made SpeedFilters. Each method hands back a lambda: a little piece of
// code that runs later, every time someone calls apply(speed) on it.
public class Filters {
    // TODO: return a lambda that halves the speed (0.8 becomes 0.4).
    public static SpeedFilter half() {
        return null;
    }

    // TODO: return a lambda that flips the sign of the speed (0.3 becomes -0.3).
    public static SpeedFilter invert() {
        return null;
    }

    // TODO: return a lambda that gives 0.0 when the speed is closer to zero
    //   than threshold (Math.abs(speed) < threshold), and the speed unchanged
    //   otherwise. It needs an if, so give it a { } body with return lines.
    public static SpeedFilter deadband(double threshold) {
        return null;
    }

    // TODO: return a lambda that runs first, then runs second on the result.
    //   chain(half(), invert()) turns 0.8 into -0.4.
    public static SpeedFilter chain(SpeedFilter first, SpeedFilter second) {
        return null;
    }
}
