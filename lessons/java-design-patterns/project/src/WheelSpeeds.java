// How fast to run each side of the drivetrain, from -1.0 (full reverse) to
// 1.0 (full forward).
public class WheelSpeeds {
    protected double left_;
    protected double right_;

    public WheelSpeeds(double left, double right) {
        left_ = left;
        right_ = right;
    }

    public double getLeft() {
        return left_;
    }

    public double getRight() {
        return right_;
    }

    @Override
    public String toString() {
        return String.format("left %.2f, right %.2f", left_, right_);
    }
}
