public class ScaleJoystickCheck {
    public static void main(String[] args) {
        check(0.5, 1.0, 0.5);
        check(0.5, 0.5, 0.25);
        check(1.0, 2.0, 1.0);
        check(-1.0, 2.0, -1.0);
        check(0.0, 3.0, 0.0);
        System.out.println("scaleJoystick looks good.");
    }

    static void check(double rawInput, double sensitivity, double expected) {
        double actual = Main.scaleJoystick(rawInput, sensitivity);
        if (Math.abs(actual - expected) > 0.0001) {
            System.out.println(
                "scaleJoystick(" + rawInput + ", " + sensitivity + ") returned " + actual + ", expected " + expected + "."
            );
            System.exit(1);
        }
    }
}
