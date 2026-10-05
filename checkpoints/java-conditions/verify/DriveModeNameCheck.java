public class DriveModeNameCheck {
    public static void main(String[] args) {
        check(1, "Tank");
        check(2, "Arcade");
        check(3, "Swerve");
        check(4, "Field-Oriented Swerve");
        check(0, "Unknown mode");
        check(5, "Unknown mode");
        System.out.println("driveModeName looks good.");
    }

    static void check(int mode, String expected) {
        String actual = Main.driveModeName(mode);
        if (!expected.equals(actual)) {
            System.out.println("driveModeName(" + mode + ") returned " + actual + ", expected " + expected + ".");
            System.exit(1);
        }
    }
}
