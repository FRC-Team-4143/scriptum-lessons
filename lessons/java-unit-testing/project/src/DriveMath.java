// PROVIDED: this code is already correct. Don't edit it: your job is to
// write tests (in DriveMathTest.java) that prove it.
public class DriveMath {
    // Joysticks never rest at exactly 0. Anything closer to 0 than threshold
    // (on either side) becomes 0, so the robot doesn't creep. Anything else
    // passes through unchanged.
    public static double deadband(double value, double threshold) {
        if (Math.abs(value) < threshold) {
            return 0.0;
        }
        return value;
    }

    // Keeps value between min and max: min if it's below, max if it's above,
    // and value itself otherwise.
    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    // Field drawings are in inches; WPILib wants meters. 1 inch = 0.0254 m.
    // Negative distances (backwards) stay negative.
    public static double inchesToMeters(double inches) {
        return inches * 0.0254;
    }
}
