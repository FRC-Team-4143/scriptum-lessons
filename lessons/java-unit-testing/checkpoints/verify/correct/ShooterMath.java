// The real, correct version the student's tests must pass on.
public class ShooterMath {
    // True when the flywheel is close enough to the target speed to shoot:
    // current_rpm is at most tolerance_rpm away from target_rpm, either
    // too fast or too slow.
    public static boolean isAtSpeed(double current_rpm, double target_rpm, double tolerance_rpm) {
        return Math.abs(current_rpm - target_rpm) <= tolerance_rpm;
    }
}
