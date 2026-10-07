public class ClampCheck {
    public static void main(String[] args) {
        check(5.0, 0.0, 10.0, 5.0);
        check(-3.0, 0.0, 10.0, 0.0);
        check(15.0, 0.0, 10.0, 10.0);
        check(-0.9, -1.0, 1.0, -0.9);
        check(1.0, -1.0, 1.0, 1.0);
        System.out.println("clamp looks good.");
    }

    static void check(double value, double min, double max, double expected) {
        double actual = Main.clamp(value, min, max);
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println(
                "clamp(" + value + ", " + min + ", " + max + ") returned " + actual + ", expected " + expected + "."
            );
            System.exit(1);
        }
    }
}
