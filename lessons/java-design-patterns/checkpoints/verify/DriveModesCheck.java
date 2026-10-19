public class DriveModesCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static DriveMode asMode(Class<?> type, Object made) {
        if (!DriveMode.class.isAssignableFrom(type)) {
            fail(type.getSimpleName() + " isn't a DriveMode yet. Add \"implements DriveMode\" to its class line.");
        }
        return (DriveMode) made;
    }

    static void expect(DriveMode mode, Gamepad pad, double left, double right, String what) {
        WheelSpeeds got = mode.calculate(pad);
        if (got == null) {
            fail(mode.getName() + " calculate() returned null " + what + ".");
        }
        if (Math.abs(got.getLeft() - left) > 0.0001 || Math.abs(got.getRight() - right) > 0.0001) {
            fail(mode.getName() + " " + what + " gave left " + got.getLeft() + ", right " + got.getRight()
                + ", expected left " + left + ", right " + right + ".");
        }
    }

    public static void main(String[] args) {
        DriveMode tank = asMode(TankDrive.class, (Object) new TankDrive());
        if (!"Tank".equals(tank.getName())) {
            fail("TankDrive's getName() was \"" + tank.getName() + "\", expected \"Tank\".");
        }
        expect(tank, new Gamepad(0.7, 0.3, 0.9), 0.7, 0.3, "with left stick 0.7, right stick 0.3 (and right stick X 0.9, which tank ignores)");
        expect(tank, new Gamepad(-0.4, 0.55, 0.0), -0.4, 0.55, "with left stick -0.4, right stick 0.55");
        expect(tank, new Gamepad(0.0, 0.0, -1.0), 0.0, 0.0, "with both sticks still");

        DriveMode arcade = asMode(ArcadeDrive.class, (Object) new ArcadeDrive());
        if (!"Arcade".equals(arcade.getName())) {
            fail("ArcadeDrive's getName() was \"" + arcade.getName() + "\", expected \"Arcade\".");
        }
        expect(arcade, new Gamepad(0.5, 0.0, 0.0), 0.5, 0.5, "going straight at 0.5");
        expect(arcade, new Gamepad(0.4, 0.9, 0.2), 0.6, 0.2,
            "with forward 0.4 and turn 0.2 (and right stick Y 0.9, which arcade ignores)");
        expect(arcade, new Gamepad(0.0, 0.0, -0.3), -0.3, 0.3, "spinning in place with turn -0.3");
        expect(arcade, new Gamepad(0.8, 0.0, 0.5), 1.0, 0.3, "with forward 0.8 and turn 0.5 (1.3 is too fast, so clamp it)");
        expect(arcade, new Gamepad(-0.6, 0.0, 0.7), 0.1, -1.0, "with forward -0.6 and turn 0.7 (-1.3 is too fast backwards)");
        expect(arcade, new Gamepad(1.0, 0.0, 1.0), 1.0, 0.0, "full forward and full turn");

        System.out.println("Tank and Arcade are both drive modes.");
    }
}
