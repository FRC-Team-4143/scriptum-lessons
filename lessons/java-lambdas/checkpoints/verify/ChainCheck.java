public class ChainCheck {
    static int calls_ = 0;

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        // Filters the lesson never mentions: chain has to work for any two.
        SpeedFilter add_one = speed -> {
            calls_++;
            return speed + 1;
        };
        SpeedFilter times_ten = speed -> {
            calls_++;
            return speed * 10;
        };

        SpeedFilter both = Filters.chain(add_one, times_ten);
        if (both == null) {
            fail("Filters.chain(...) returned null. Return a lambda that runs first, then second.");
        }
        if (calls_ != 0) {
            fail("Filters.chain ran a filter while building the chain. It should only return a lambda; the filters run later, when apply is called.");
        }

        double got = both.apply(2.0);
        if (Math.abs(got - 30.0) > 0.0001) {
            if (Math.abs(got - 21.0) < 0.0001) {
                fail("chain(add one, times ten).apply(2.0) was 21.0: it ran second before first. Expected (2 + 1) x 10 = 30.0.");
            }
            fail("chain(add one, times ten).apply(2.0) was " + got + ", expected (2 + 1) x 10 = 30.0.");
        }
        if (calls_ != 2) {
            fail("One apply on a chain should run each filter once, but they ran " + calls_ + " times in total.");
        }

        got = Filters.chain(times_ten, add_one).apply(2.0);
        if (Math.abs(got - 21.0) > 0.0001) {
            fail("chain(times ten, add one).apply(2.0) was " + got + ", expected 2 x 10 + 1 = 21.0.");
        }

        // A chain is a SpeedFilter too, so chains can go inside chains.
        SpeedFilter three = Filters.chain(Filters.chain(add_one, add_one), times_ten);
        got = three.apply(0.0);
        if (Math.abs(got - 20.0) > 0.0001) {
            fail("chain(chain(add one, add one), times ten).apply(0.0) was " + got + ", expected 20.0.");
        }

        System.out.println("chain builds new behavior out of two filters.");
    }
}
