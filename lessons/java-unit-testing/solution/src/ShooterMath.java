// This one has a BUG in it. Write a test in ShooterMathTest.java that
// catches it first, see the test FAIL, then fix the bug here.
public class ShooterMath {
    // True when the flywheel is close enough to the target speed to shoot:
    // current_rpm is at most tolerance_rpm away from target_rpm, either
    // too fast or too slow.
    public static boolean isAtSpeed(double current_rpm, double target_rpm, double tolerance_rpm) {
        return Math.abs(current_rpm - target_rpm) <= tolerance_rpm;
    }
}
