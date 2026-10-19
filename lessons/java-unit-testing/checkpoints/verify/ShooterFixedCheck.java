// Checks the project's own ShooterMath.isAtSpeed after the student's fix.
public class ShooterFixedCheck {
    static void expect(double current, double target, double tolerance, boolean want) {
        boolean got = ShooterMath.isAtSpeed(current, target, tolerance);
        if (got != want) {
            double off = Math.abs(current - target);
            System.out.println("Your tests catch the bug. Now fix ShooterMath: isAtSpeed(" + current + ", " + target
                + ", " + tolerance + ") returned " + got + ", but " + current + " rpm is " + off
                + " away from the target and the tolerance is " + tolerance + ", so it should be " + want + ".");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        expect(3000.0, 3000.0, 50.0, true);
        expect(1000.0, 3000.0, 50.0, false);
        expect(0.0, 3000.0, 50.0, false);
        expect(2960.0, 3000.0, 50.0, true);
        expect(3040.0, 3000.0, 50.0, true);
        expect(3200.0, 3000.0, 50.0, false);
        expect(2900.0, 3000.0, 50.0, false);
        expect(3050.0, 3000.0, 50.0, true);
        expect(2950.0, 3000.0, 50.0, true);
        expect(-3020.0, -3000.0, 50.0, true);
        expect(-2000.0, -3000.0, 50.0, false);
        System.out.println("Your tests caught the bug, and isAtSpeed is fixed: the shooter waits until the flywheel is really up to speed.");
    }
}
