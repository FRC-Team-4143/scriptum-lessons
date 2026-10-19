// A way of turning the driver's sticks into wheel speeds. Each drive mode is
// one "strategy": the drivetrain doesn't care which one it has.
public interface DriveMode {
    // What the driver sees on the dashboard, like "Tank".
    String getName();

    // The wheel speeds for this reading of the controller.
    WheelSpeeds calculate(Gamepad pad);
}
