import java.util.ArrayList;
import java.util.List;

public class SwapModesCheck {
    // Drive modes the lesson never mentions: the drivetrain has to work with any DriveMode.
    static class Spin implements DriveMode {
        public String getName() { return "Spin"; }
        public WheelSpeeds calculate(Gamepad pad) { return new WheelSpeeds(0.35, -0.35); }
    }

    static class Gentle implements DriveMode {
        List<Gamepad> seen_ = new ArrayList<>();
        public String getName() { return "Gentle"; }
        public WheelSpeeds calculate(Gamepad pad) {
            seen_.add(pad);
            return new WheelSpeeds(pad.getLeftY() * 0.25, pad.getRightY() * 0.25);
        }
    }

    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void expect(Drivetrain drivetrain, Gamepad pad, double left, double right, String what) {
        WheelSpeeds got = drivetrain.drive(pad);
        if (got == null) {
            fail("drive() returned null " + what + ". Return what the mode's calculate() gives you.");
        }
        if (Math.abs(got.getLeft() - left) > 0.0001 || Math.abs(got.getRight() - right) > 0.0001) {
            fail("drive() " + what + " gave left " + got.getLeft() + ", right " + got.getRight()
                + ", expected left " + left + ", right " + right + ". Is it asking the current mode?");
        }
    }

    static void expectName(Drivetrain drivetrain, String want, String what) {
        String got = drivetrain.getModeName();
        if (!want.equals(got)) {
            fail("getModeName() " + what + " was \"" + got + "\", expected \"" + want + "\".");
        }
    }

    public static void main(String[] args) {
        Spin spin = new Spin();
        Gentle gentle = new Gentle();

        Drivetrain drivetrain = new Drivetrain(spin);
        expectName(drivetrain, "Spin", "for a drivetrain built with a Spin mode");
        expect(drivetrain, new Gamepad(0.9, 0.9, 0.0), 0.35, -0.35, "with a Spin mode (it always spins at 0.35)");

        drivetrain.setMode(gentle);
        expectName(drivetrain, "Gentle", "after setMode(a Gentle mode)");
        Gamepad first = new Gamepad(0.8, -0.4, 0.0);
        expect(drivetrain, first, 0.2, -0.1, "after switching to a Gentle mode (a quarter of each stick)");
        if (gentle.seen_.isEmpty() || gentle.seen_.get(gentle.seen_.size() - 1) != first) {
            fail("drive() should hand the Gamepad it was given to the mode's calculate().");
        }
        expect(drivetrain, new Gamepad(-1.0, 0.6, 0.0), -0.25, 0.15, "on the next loop, with new stick readings");

        drivetrain.setMode(null);
        expectName(drivetrain, "Gentle", "after setMode(null) (it should keep the mode it had)");
        expect(drivetrain, new Gamepad(0.4, 0.4, 0.0), 0.1, 0.1, "after setMode(null)");

        drivetrain.setMode(spin);
        expectName(drivetrain, "Spin", "after switching back to Spin");
        expect(drivetrain, new Gamepad(0.4, 0.4, 0.0), 0.35, -0.35, "after switching back to Spin");

        Drivetrain other = new Drivetrain(gentle);
        expectName(other, "Gentle", "for a second drivetrain built with a Gentle mode");
        expectName(drivetrain, "Spin", "for the first drivetrain, after a second one was built (use a field, not a static one)");

        System.out.println("The drivetrain drives with whatever mode it's given.");
    }
}
