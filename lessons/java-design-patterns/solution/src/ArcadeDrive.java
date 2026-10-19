// Arcade drive: one stick says how fast to go, the other says how hard to
// turn, like a video game.
public class ArcadeDrive implements DriveMode {
    @Override
    public String getName() {
        return "Arcade";
    }

    @Override
    public WheelSpeeds calculate(Gamepad pad) {
        double forward = pad.getLeftY();
        double turn = pad.getRightX();
        return new WheelSpeeds(clamp(forward + turn), clamp(forward - turn));
    }

    private static double clamp(double speed) {
        return Math.max(-1.0, Math.min(1.0, speed));
    }
}
