import java.util.function.DoubleSupplier;

public class MethodRefsCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static void press(Controller c, boolean a, boolean b, boolean x) {
        c.setA(a);
        c.setB(b);
        c.setX(x);
    }

    static void expectPower(Intake intake, double want, String what) {
        if (Math.abs(intake.getPower() - want) > 0.0001) {
            fail(what + ", the intake's power was " + intake.getPower() + ", expected " + want + ".");
        }
    }

    static void expectSpeed(double got, double want, String what) {
        if (Math.abs(got - want) > 0.0001) {
            fail(what + " was " + got + ", expected " + want + ".");
        }
    }

    public static void main(String[] args) {
        try {
            check();
        } catch (NullPointerException e) {
            fail("Something was null: " + e.getMessage() + ". This checkpoint uses your work from the"
                + " earlier ones too (Filters.deadband and ButtonBoard), so finish those first.");
        }
        System.out.println("The robot's buttons and stick are wired up with lambdas and method references.");
    }

    static void check() {
        Controller controller = new Controller();
        Intake intake = new Intake();
        Drivetrain drivetrain = new Drivetrain();
        RobotBindings robot = new RobotBindings(controller, intake, drivetrain);
        robot.configureBindings();
        expectPower(intake, 0.0, "Right after configureBindings(), before any button is pressed");

        robot.periodic();
        expectPower(intake, 0.0, "After a loop with no buttons pressed");

        press(controller, true, false, false);
        robot.periodic();
        expectPower(intake, 1.0,
            "After pressing A (bind controller_::a to intake_::run, and check ButtonBoard.poll works)");

        press(controller, true, true, false);
        robot.periodic();
        expectPower(intake, 0.0, "After pressing B (bind controller_::b to intake_::stop)");

        press(controller, true, true, false);
        robot.periodic();
        expectPower(intake, 0.0, "Holding A and B for another loop (A was never let go, so it shouldn't run again)");

        press(controller, false, false, true);
        robot.periodic();
        expectPower(intake, -0.5, "After pressing X (bind controller_::x to () -> intake_.setPower(EJECT_POWER))");

        press(controller, true, false, false);
        robot.periodic();
        expectPower(intake, 1.0, "After pressing A again");

        // The supplier is made first and the stick moves afterwards: it has to
        // read the stick each time it is asked, not once when it was made.
        DoubleSupplier speed = robot.driveSpeed();
        if (speed == null) {
            fail("driveSpeed() returned null. Return a lambda that reads the stick.");
        }
        controller.setLeftY(-0.6);
        expectSpeed(speed.getAsDouble(), 0.6,
            "With the stick pushed forward (getLeftY() = -0.6), driveSpeed().getAsDouble()");
        controller.setLeftY(0.9);
        expectSpeed(speed.getAsDouble(), -0.9,
            "With the stick pulled back (getLeftY() = 0.9), the same supplier's getAsDouble()");
        controller.setLeftY(0.05);
        expectSpeed(speed.getAsDouble(), 0.0,
            "With the stick barely touched (getLeftY() = 0.05), getAsDouble() (use stick_filter_ for the deadband)");

        controller.setLeftY(-0.4);
        robot.periodic();
        expectSpeed(drivetrain.getSpeed(), 0.4, "After a loop with getLeftY() = -0.4, the drivetrain's speed");
    }
}
