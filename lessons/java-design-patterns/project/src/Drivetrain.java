// The drivetrain holds ONE drive mode, and hands every controller reading to
// it. It never checks which mode it has: swap the mode, and the same code
// drives differently. That's the Strategy pattern.
public class Drivetrain {
    protected DriveMode mode_;

    // TODO: remember starting_mode in mode_.
    public Drivetrain(DriveMode starting_mode) {
    }

    // TODO: switch to a new mode. setMode(null) is ignored: the robot keeps
    //   the mode it already has.
    public void setMode(DriveMode mode) {
    }

    // TODO: the name of the mode the drivetrain is using now.
    public String getModeName() {
        return "";
    }

    // TODO: ask the current mode for the wheel speeds, and return them.
    public WheelSpeeds drive(Gamepad pad) {
        return new WheelSpeeds(0.0, 0.0);
    }
}
