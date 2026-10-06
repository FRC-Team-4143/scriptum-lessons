public class MetersToInchesCheck {
    public static void main(String[] args) {
        check(Main.metersToInches(1.0), 39.3701, "metersToInches(1.0)");
        check(Main.metersToInches(0), 0.0, "metersToInches(0)");
        check(Main.metersToInches(0.508), 20.0, "metersToInches(0.508)");
        System.out.println("metersToInches looks good.");
    }

    static void check(double actual, double expected, String label) {
        if (Math.abs(actual - expected) > 0.001) {
            System.out.println(label + " returned " + actual + ", expected about " + expected + ".");
            System.exit(1);
        }
    }
}
