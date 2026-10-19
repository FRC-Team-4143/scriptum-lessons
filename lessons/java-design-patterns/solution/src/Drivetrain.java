// The drivetrain holds ONE drive mode, and hands every controller reading to
// it. It never checks which mode it has: swap the mode, and the same code
// drives differently. That's the Strategy pattern.
public class Drivetrain {
    protected DriveMode mode_;

    public Drivetrain(DriveMode starting_mode) {
        mode_ = starting_mode;
    }

    public void setMode(DriveMode mode) {
        if (mode != null) {
            mode_ = mode;
        }
    }

    public String getModeName() {
        return mode_.getName();
    }

    public WheelSpeeds drive(Gamepad pad) {
        return mode_.calculate(pad);
    }
}
