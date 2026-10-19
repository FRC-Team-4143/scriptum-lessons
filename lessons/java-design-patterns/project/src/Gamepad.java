// One reading of the driver's controller. Each stick axis goes from -1.0 to
// 1.0, and in this lesson pushing a stick forward gives a positive number.
public class Gamepad {
    protected double left_y_;
    protected double right_y_;
    protected double right_x_;

    public Gamepad(double left_y, double right_y, double right_x) {
        left_y_ = left_y;
        right_y_ = right_y;
        right_x_ = right_x;
    }

    // Left stick, forward and back.
    public double getLeftY() {
        return left_y_;
    }

    // Right stick, forward and back.
    public double getRightY() {
        return right_y_;
    }

    // Right stick, left (negative) and right (positive).
    public double getRightX() {
        return right_x_;
    }
}
