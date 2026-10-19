// Ready-made SpeedFilters. Each method hands back a lambda: a little piece of
// code that runs later, every time someone calls apply(speed) on it.
public class Filters {
    public static SpeedFilter half() {
        return speed -> speed * 0.5;
    }

    public static SpeedFilter invert() {
        return speed -> -speed;
    }

    public static SpeedFilter deadband(double threshold) {
        return speed -> {
            if (Math.abs(speed) < threshold) {
                return 0.0;
            }
            return speed;
        };
    }

    public static SpeedFilter chain(SpeedFilter first, SpeedFilter second) {
        return speed -> second.apply(first.apply(speed));
    }
}
