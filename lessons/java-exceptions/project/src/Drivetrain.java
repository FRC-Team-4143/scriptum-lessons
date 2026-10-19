// A pretend drivetrain: it just remembers the speed it was last told to drive.
// This file is already finished.
public class Drivetrain {
    protected double speed_ = 0.0;

    public void setSpeed(double speed) {
        speed_ = speed;
    }

    public void stop() {
        speed_ = 0.0;
    }

    public double getSpeed() {
        return speed_;
    }
}
