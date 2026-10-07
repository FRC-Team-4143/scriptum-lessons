public class MotorDirectionCheck {
    public static void main(String[] args) {
        check(Main.motorDirection(true), "REVERSED", "motorDirection(true)");
        check(Main.motorDirection(false), "FORWARD", "motorDirection(false)");
        System.out.println("motorDirection looks good.");
    }

    static void check(String actual, String expected, String label) {
        if (!expected.equals(actual)) {
            System.out.println(label + " returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
