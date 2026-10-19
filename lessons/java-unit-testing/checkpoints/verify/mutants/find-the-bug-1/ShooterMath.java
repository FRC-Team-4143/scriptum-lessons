// Slips through: The original, buggy isAtSpeed still passes all your tests, so they haven't caught the bug yet. Try speeds on both sides of the target, some close and some far away.
public class ShooterMath {
    // True when the flywheel is close enough to the target speed to shoot:
    // current_rpm is at most tolerance_rpm away from target_rpm, either
    // too fast or too slow.
    public static boolean isAtSpeed(double current_rpm, double target_rpm, double tolerance_rpm) {
        return current_rpm - target_rpm <= tolerance_rpm;
    }
}
