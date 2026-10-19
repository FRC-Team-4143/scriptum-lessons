// Tank drive: the left stick runs the left wheels and the right stick runs
// the right wheels, like driving a tank with two levers.
public class TankDrive implements DriveMode {
    @Override
    public String getName() {
        return "Tank";
    }

    @Override
    public WheelSpeeds calculate(Gamepad pad) {
        return new WheelSpeeds(pad.getLeftY(), pad.getRightY());
    }
}
